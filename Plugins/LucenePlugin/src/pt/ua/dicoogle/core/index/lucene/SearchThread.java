/*  Copyright   2012 - IEETA
 *
 *  This file is part of Dicoogle.
 *
 *  Author: Luís A. Bastião Silva <bastiao@ua.pt>
 *
 *  Dicoogle is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Dicoogle is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ua.dicoogle.core.index.lucene;

import java.util.List;
import pt.ua.dicoogle.sdk.Utils.SearchResult;
import pt.ua.dicoogle.sdk.observables.ListObservableSearch;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class SearchThread extends Thread
{
    
    private ListObservableSearch<SearchResult> lo;
    private IndexCore index;
    private String searcStr;
    private List<String> al;
    public SearchThread(String search, ListObservableSearch<SearchResult> lo, IndexCore index,List<String> al)
    {
        this.lo = lo;
        this.index = index;
        this.al = al;
        this.searcStr = search;
    }
    
    public void run()
    {
        index.search(searcStr, al, IndexCore.BLOCKS_SEARCH, lo);
    
    }

    
}
