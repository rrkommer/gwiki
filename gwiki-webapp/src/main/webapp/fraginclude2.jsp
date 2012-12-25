<%@ page import="de.micromata.genome.gwiki.web.GWikiSnippets"%>
<%
GWikiSnippets wikisnip = new GWikiSnippets(pageContext); 
%>
<html>
<body>
<h1>GWiki Fragment include sample.</h1>
Via Snippet:<br/>
<% wikisnip.include("home/rkommer/WikiFragmentBeispiel"); %>
<p/>
Via include:<br/>
<% pageContext.include("gwiki/home/rkommer/WikiFragmentBeispiel"); %>
</body>
</html>