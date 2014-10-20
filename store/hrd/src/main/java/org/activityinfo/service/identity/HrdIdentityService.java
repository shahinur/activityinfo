package org.activityinfo.service.identity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.service.identity.entity.UserAccount;
import org.activityinfo.service.identity.entity.UserAccountKey;
import org.activityinfo.store.hrd.tx.ReadTx;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HrdIdentityService implements IdentityService {

    @Override
    public Map<UserId, String> getUserNames(Set<UserId> userIds) {

        List<UserAccountKey> userKeys = Lists.newArrayList();
        for(UserId userId : userIds) {
            userKeys.add(new UserAccountKey(userId));
        }

        try(ReadTx tx = ReadTx.outsideTransaction()) {
            Map<UserId, String> nameMap = Maps.newHashMap();
            for(UserAccount account : tx.getList(userKeys)) {
                nameMap.put(account.getUserId(), String.format("\"%s\" <%s>", account.getName(), account.getEmail()));
            }
            return nameMap;
        }
    }
}
