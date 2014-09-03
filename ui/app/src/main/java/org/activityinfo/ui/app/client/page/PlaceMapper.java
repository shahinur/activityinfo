package org.activityinfo.ui.app.client.page;

import com.google.common.collect.Lists;
import org.activityinfo.ui.app.client.page.create.NewWorkspacePlace;
import org.activityinfo.ui.app.client.page.folder.FolderPlace;
import org.activityinfo.ui.app.client.page.form.FormPlace;
import org.activityinfo.ui.app.client.page.home.HomePlace;

import java.util.List;

public class PlaceMapper {

    private final List<PlaceParser> parsers = Lists.newArrayList();

    public PlaceMapper() {
        parsers.add(new FormPlace.Parser());
        parsers.add(new FolderPlace.Parser());
        parsers.add(new NewWorkspacePlace.Parser());
    }

    public Place parse(String url) {
        String tokens[] = parseToken(url);
        for(PlaceParser tokenizer : parsers) {
            Place place = tokenizer.tryParse(tokens);
            if(place != null) {
                return place;
            }
        }
        return new HomePlace();
    }


    private static String[] parseToken(String token) {
        if(token.length() == 0) {
            return new String[0];
        } else {
            return token.split("/");
        }
    }
}
