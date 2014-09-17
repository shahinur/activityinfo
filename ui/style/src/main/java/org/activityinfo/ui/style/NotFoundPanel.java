package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class NotFoundPanel extends VComponent {


    @Override
    protected VTree render() {
//        <div class="notfoundpanel">
//        <h1>404!</h1>
//        <h3>The page you are looking for has not been found!</h3>
//        <h4>The page you are looking for might have been removed, had its name changed, or unavailable. <br>Maybe you could try a search:</h4>
//        <form action="search-results.html">
//        <input type="text" class="form-control" placeholder="Search for page"> <button class="btn btn-success">Search</button>
//        </form>
//        </div>

        return div(BaseStyles.NOTFOUNDPANEL, h1("404!"),
            h3("The page you are looking for has not been found!"),
            h4("The page you are looking for might have been removed"));

    }
}
