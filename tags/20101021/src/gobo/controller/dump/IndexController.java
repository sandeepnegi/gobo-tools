package gobo.controller.dump;

import gobo.AuthSubBase;
import gobo.service.GbDatastoreService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

public class IndexController extends AuthSubBase {

	@Override
	protected String runAuth() throws Exception {

		List<String> kinds = GbDatastoreService.getKinds();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (String kind : kinds) {
			Map<String, Object> row = new HashMap<String, Object>();
			int count =
				datastore.prepare(new Query(kind)).countEntities(
					FetchOptions.Builder.withDefaults());
			if (count == 0) {
				continue;
			}
			row.put("name", kind);
			row.put("count", count);
			list.add(row);
		}
		requestScope("list", list);

		return forward("/gobo/dump/index.jsp");
	}
}
