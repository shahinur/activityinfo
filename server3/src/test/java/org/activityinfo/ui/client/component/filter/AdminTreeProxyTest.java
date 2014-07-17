package org.activityinfo.ui.client.component.filter;

import com.google.common.collect.Sets;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.model.AdminEntityDTO;
import org.activityinfo.legacy.shared.model.EntityDTO;
import org.activityinfo.legacy.shared.util.Collector;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class AdminTreeProxyTest extends CommandTestCase2 {

    @Test
    public void test() {

        setUser(3);

        AdminTreeProxy proxy = new AdminTreeProxy(getDispatcher());
        proxy.setFilter(Filter.filter().onActivity(2));

        Collector<List<AdminEntityDTO>> collector = new Collector<>();
        proxy.load(null, collector);

        assertThat(collector.getResult(), isSetOf("Ituri", "Sud Kivu"));
        assertThat(collector.getResult(), isSetOf("Ituri", "Sud Kivu"));

    }

    private Matcher<Iterable<? extends EntityDTO>> isSetOf(final String... names) {
        return new TypeSafeMatcher<Iterable<? extends EntityDTO>>() {

            @Override
            protected boolean matchesSafely(Iterable<? extends EntityDTO> list) {
                Set<String> nameSet = Sets.newHashSet(names);
                for(EntityDTO entity : list) {
                    if(!nameSet.remove(entity.getName())) {
                        return false;
                    }
                }
                if(!nameSet.isEmpty()) {
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(Arrays.toString(names));
            }
        };
    }

}
