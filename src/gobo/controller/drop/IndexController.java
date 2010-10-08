package gobo.controller.drop;

import gobo.service.GbDatastoreService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;


public class IndexController extends Controller {

	@Override
	protected Navigation run() throws Exception {
		
		List<String> kinds = GbDatastoreService.getKinds();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(String kind : kinds) {
			Map<String, Object> row = new HashMap<String, Object>();
			int count = Datastore.query(kind).count();
			row.put("name", kind);
			row.put("count", count);
			list.add(row);
		}
		requestScope("list", list);
		return forward("index.jsp");
	}

}
