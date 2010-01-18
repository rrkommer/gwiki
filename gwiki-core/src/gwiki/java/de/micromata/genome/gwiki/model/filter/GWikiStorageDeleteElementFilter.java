/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   16.11.2009
// Copyright Micromata 16.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model.filter;

/**
 * Filter which wrapps is like a servlet filter, wrapping delivery of a GWikiElement.
 * 
 * wikiContext.getElement() delivers the element to render.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiStorageDeleteElementFilter extends
    GWikiFilter<Void, GWikiStorageDeleteElementFilterEvent, GWikiStorageDeleteElementFilter>
{

}
