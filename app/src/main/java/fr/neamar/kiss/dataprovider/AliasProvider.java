package fr.neamar.kiss.dataprovider;

import android.content.Context;

import java.util.ArrayList;
import java.util.regex.Pattern;

import fr.neamar.kiss.loader.LoadAliasPojos;
import fr.neamar.kiss.pojo.AliasPojo;
import fr.neamar.kiss.pojo.Pojo;

public class AliasProvider extends Provider<AliasPojo> {
    private final AppProvider appProvider;

    public AliasProvider(final Context context, AppProvider appProvider) {
        super(new LoadAliasPojos(context));
        this.appProvider = appProvider;
    }

    public ArrayList<Pojo> getResults(String query) {
        ArrayList<Pojo> results = new ArrayList<>();

        for (AliasPojo entry : pojos) {
            if (entry.alias.startsWith(query)) {
                // Retrieve the AppPojo from AppProvider, being careful not to create any side effect
                // (default behavior is to alter displayName, which is not what we want)
                Pojo appPojo = appProvider.findById(entry.app, false);
                // Only add if default AppProvider is not already displaying it
                if (appPojo != null && !appPojo.nameLowerCased.contains(query)) {
                    appPojo.displayName = appPojo.name
                            + " <small>("
                            + entry.alias.replaceFirst(
                            "(?i)(" + Pattern.quote(query) + ")", "{$1}")
                            + ")</small>";
                    appPojo.relevance = 10;
                    results.add(appPojo);
                }
            }
        }

        return results;
    }
}
