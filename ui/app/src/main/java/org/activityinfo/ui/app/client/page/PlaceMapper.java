package org.activityinfo.ui.app.client.page;

import com.google.common.collect.Lists;
import org.activityinfo.ui.app.client.page.folder.FolderPlace;
import org.activityinfo.ui.app.client.page.form.FormPlace;
import org.activityinfo.ui.app.client.page.home.HomePlace;
import org.activityinfo.ui.app.client.place.NewWorkspacePlace;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlaceMapper {

    private static final Logger LOGGER = Logger.getLogger(PlaceMapper.class.getName());

    private final List<PlaceParser> parsers = Lists.newArrayList();

    public PlaceMapper() {
        parsers.add(new FormPlace.Parser());
        parsers.add(new FolderPlace.Parser());
        parsers.add(new ResourcePlace.Parser());
        parsers.add(new NewWorkspacePlace.Parser());
    }

    public Place parse(String url) {
        String tokens[] = parseToken(url);
        for(PlaceParser tokenizer : parsers) {
            try {
                Place place = tokenizer.tryParse(tokens);
                if (place != null) {
                    return place;
                }
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Failed to parse tokens: " + Arrays.toString(tokens) + ". " + e.getMessage());
            }
        }
        return HomePlace.INSTANCE;
    }

    private static String[] parseToken(String token) {
        if(token.length() == 0) {
            return new String[0];
        } else {
            return token.split("/");
        }
    }
}
