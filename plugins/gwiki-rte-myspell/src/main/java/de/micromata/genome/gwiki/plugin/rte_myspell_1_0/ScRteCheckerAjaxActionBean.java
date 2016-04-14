//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.plugin.rte_myspell_1_0;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.dts.spell.SpellChecker;
import org.dts.spell.dictionary.OpenOfficeSpellDictionary;
import org.dts.spell.dictionary.SpellDictionary;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class ScRteCheckerAjaxActionBean extends ActionBeanBase
{
  private static final String DEFAULT_LANGUAGE = "en";

  private int maxSuggestionsCount = 25;

  /**
   * TODO later instead of caching, use GWiki element cache.
   */
  private Map<String, SpellChecker> spellcheckersCache = new Hashtable<String, SpellChecker>(2);

  /**
   * TODO later other way
   */
  private String pathToDictionaries = "edit/spellchecker/dictionaries/";

  private enum Methods
  {
    checkWords, getSuggestions
  }

  public Object onInit()
  {
    setResponeHeaders();
    JSONObject jsonInput = readRequest();
    String methodName = jsonInput.optString("method");
    if (methodName == null || methodName.trim().equals("")) {
      throw new ScSpellException("Wrong spellchecker-method-name:" + methodName);
    }
    try {
      JSONObject jsonOutput = JSONObject.fromObject("{'id':null,'result':[],'error':null}");
      // JSONObject jsonOutput = JSONObject.fromObject("{'id':null,'result':[],'error':null}", null);
      switch (Methods.valueOf(methodName.trim())) {
        case checkWords:
          jsonOutput.put("result", checkWords(jsonInput.optJSONArray("params")));
          break;
        case getSuggestions:
          jsonOutput.put("result", getSuggestions(jsonInput.optJSONArray("params")));
          break;
        default:
          throw new ScSpellException("Unimplemented spellchecker method {" + methodName + "}");
      }

      // PrintWriter pw = response.getWriter();
      // pw.println(jsonOutput.toString());
      wikiContext.append(jsonOutput.toString());
    } catch (ScSpellException se) {
      GWikiLog.warn("Spell error: " + se.getMessage(), se);
      // logger.log(Level.WARNING, se.getMessage(), se);
      returnError(se.getMessage());
    } catch (Exception e) {
      GWikiLog.warn("Spell error: " + e.getMessage(), e);
      returnError(e.getMessage());
    }
    wikiContext.flush();
    // response.getWriter().flush();
    return noForward();
  }

  private JSONArray checkWords(JSONArray params) throws ScSpellException
  {
    JSONArray misspelledWords = new JSONArray();
    if (params != null) {
      JSONArray checkedWords = params.optJSONArray(1);
      String lang = params.optString(0);
      lang = ("".equals(lang)) ? DEFAULT_LANGUAGE : lang;

      List<String> misspelledWordsList = findMisspelledWords(new JsonArrayIterator(checkedWords), lang);

      for (String misspelledWord : misspelledWordsList) {
        misspelledWords.add(misspelledWord);
      }

    }
    return misspelledWords;
  }

  private JSONArray getSuggestions(JSONArray params) throws ScSpellException
  {
    JSONArray suggestions = new JSONArray();
    if (params != null) {
      String lang = params.optString(0);
      lang = ("".equals(lang)) ? DEFAULT_LANGUAGE : lang;
      String word = params.optString(1);
      List<String> suggestionsList = findSuggestions(word, lang, maxSuggestionsCount);
      for (String suggestion : suggestionsList) {
        suggestions.add(suggestion);
      }
    }
    return suggestions;
  }

  protected void preloadLanguageChecker(String preloadedLanguage) throws ScSpellException
  {
    getChecker(preloadedLanguage);
  }

  protected List<String> findMisspelledWords(Iterator<String> checkedWordsIterator, String lang) throws ScSpellException
  {
    SpellChecker checker = (SpellChecker) getChecker(lang);

    List<String> misspelledWordsList = new ArrayList<String>();
    while (checkedWordsIterator.hasNext()) {
      String word = checkedWordsIterator.next();
      if (!word.equals("") && !checker.isCorrect(word)) {
        misspelledWordsList.add(word);
      }
    }

    return misspelledWordsList;
  }

  protected List<String> findSuggestions(String word, String lang, int maxSuggestions) throws ScSpellException
  {
    SpellChecker checker = getChecker(lang);
    return checker.getDictionary().getSuggestions(word, maxSuggestions);
  }

  /**
   * This method look for the already created SpellChecker object in the cache, if it is not present in the cache then it try to load it and
   * put newly created object in the cache. SpellChecker loading is quite expensive operation to do it for every spell-checking request, so
   * in-memory-caching here is almost a "MUST to have"
   * 
   * @param lang the language code like "en" or "en-us"
   * @return instance of jazzy SpellChecker
   * @throws SpellCheckException if method failed to load the SpellChecker for lang (it happens if there is no dictionaries for that
   *           language was found in the classpath
   */
  protected SpellChecker getChecker(String lang) throws ScSpellException
  {
    SpellChecker cachedCheker = spellcheckersCache.get(lang);
    if (cachedCheker == null) {
      cachedCheker = loadSpellChecker(lang);
      spellcheckersCache.put(lang, cachedCheker);
    }
    return cachedCheker;
  }

  /**
   * Load the SpellChecker object from the file-system.
   * 
   * @param lang
   * @return loaded SpellChecker object
   * @throws SpellCheckException
   */
  private SpellChecker loadSpellChecker(final String lang) throws ScSpellException
  {
    String fname = pathToDictionaries + lang + ".zip";
    FileSystem fs = wikiContext.getWikiWeb().getStorage().getFileSystem();
    if (fs.exists(fname) == false) {
      throw new ScSpellException("Failed to load dictionary for language: " + lang);
    }
    byte[] data = fs.readBinaryFile(fname);
    // String pathToDictionaries = pathToDictionaries;
    // File dictionariesDir = new File(pathToDictionaries);
    // File dictionaryFile = new File(dictionariesDir, lang + ".zip");

    SpellDictionary dict = null;
    try {
      dict = new OpenOfficeSpellDictionary(new ByteArrayInputStream(data), (File) null);
      // dict = new OpenOfficeSpellDictionary(new ZipFile(dictionaryFile));
    } catch (IOException e) {
      throw new ScSpellException("Failed to load dictionary for language" + lang, e);
    }
    SpellChecker checker = new SpellChecker(dict);
    configureSpellChecker(checker);
    return checker;
  }

  private void configureSpellChecker(SpellChecker checker)
  {
    checker.setSkipNumbers(true);
    checker.setIgnoreUpperCaseWords(true);
    // set checker.setCaseSensitive(false) to avoid checking the case of
    // word (JMySpell require first word in the sentence to be upper-cased)
    checker.setCaseSensitive(false);
  }

  private void returnError(String message)
  {
    wikiContext.getResponse().setStatus(500);
    wikiContext.append("{'id':null,'response':[],'error':'" + StringEscapeUtils.escapeJavaScript(message) + "'}");
    // response.getWriter().print("{'id':null,'response':[],'error':'" + message + "'}");
    wikiContext.flush();
  }

  private void setResponeHeaders()
  {
    HttpServletResponse response = wikiContext.getResponse();
    response.setContentType("text/plain; charset=utf-8");
    response.setCharacterEncoding("utf-8");
    response.setHeader("Cache-Control", "no-store, no-cache");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", System.currentTimeMillis());
  }

  private String getRequestBody() throws IOException
  {
    StringBuilder sb = new StringBuilder();
    BufferedReader reader = wikiContext.getRequest().getReader();
    String line = reader.readLine();
    while (null != line) {
      sb.append(line);
      line = reader.readLine();
    }
    return sb.toString();
  }

  private JSONObject readRequest() throws ScSpellException
  {
    JSONObject jsonInput = null;
    try {
      jsonInput = JSONObject.fromObject(getRequestBody());
    } catch (JSONException e) {
      throw new ScSpellException("Could not interpret JSON request body", e);
    } catch (IOException e) {
      throw new ScSpellException("Error reading request body", e);
    }
    return jsonInput;
  }
}
