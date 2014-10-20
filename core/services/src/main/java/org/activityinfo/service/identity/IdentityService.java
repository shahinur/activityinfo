package org.activityinfo.service.identity;

import java.util.Map;
import java.util.Set;

public interface IdentityService {

    Map<UserId, String> getUserNames(Set<UserId> userIds);


}
