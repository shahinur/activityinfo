package org.activityinfo.store.hrd;

import com.google.common.collect.Sets;
import org.activityinfo.model.resource.Base62;
import org.junit.Rule;
import org.junit.Test;

import java.util.Set;

public class ClientIdProviderTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();

    @Test
    public void test() {

        Set<String> ids = Sets.newHashSet();

        ClientIdProvider provider = new ClientIdProvider();

        for(int i=0;i<1000;++i) {
            long id = provider.getNext();
            String encoded = Base62.encode(id);
            System.out.println(id + "  " + encoded);

            boolean added = ids.add(encoded);
            if(!added) {
                throw new AssertionError("Duplicate id generated");
            }
        }
    }
}