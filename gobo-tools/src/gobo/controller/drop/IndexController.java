package gobo.controller.drop;

import gobo.ControllerBase;
import gobo.service.GbDatastoreService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.Query;

public class IndexController extends ControllerBase {

	@Override
	protected String run() throws Exception {

		List<String> kinds = GbDatastoreService.getKinds();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (String kind : kinds) {
			Map<String, Object> row = new HashMap<String, Object>();
			int count = datastore.prepare(new Query(kind)).countEntities();
			row.put("name", kind);
			row.put("count", count);
			list.add(row);
		}
		requestScope("list", list);
		
		return forward("/gobo/drop/index.jsp");
	}

}
