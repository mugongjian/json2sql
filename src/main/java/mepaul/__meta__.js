
var main_return_class=Packages.mepaul.beansql.PersistMeta;

function main() {
	var PersistMeta = Packages.mepaul.beansql.PersistMeta;
	var HashMap = java.util.HashMap;
	var map = HashMap();
	var confs =[	
		paul
	];
	var i,conf;
	for(i=0; i<confs.length; i++) {
		conf = getTableMeta( confs[i]() );
		map.put(conf.getTableName().getKey(),conf) ;
	}
	var pMeta = PersistMeta(map);
	pMeta.setDefaultDbType('db2');
	return pMeta;
	
}
function paul() {
	return {
		tableName:['paul','PAUL'],
		field:[
			['id','ID'],
			['name','NAME']
		],
		primaryKey:['id'],
		condition:{}
	};
}


/* tool function */
function getTableMeta(tableConf) {
	var objIndex = 0;
	var dbIndex = 1;
	var typeIndex = 2;
	var TableMeta = Packages.mepaul.beansql.TableMeta;

	var table = tableConf.tableName;

	var tableMeta = TableMeta(table[objIndex], table[dbIndex]);
	var fields = tableConf.field;
	var leng = fields.length;

	for (var i = 0; i < leng; i++) {
		var f = fields[i];
		tableMeta.addFieldMeta(f[objIndex], f[dbIndex], f[typeIndex]||'string');
	}
	tableMeta.primaryKey = java_list(tableConf.primaryKey);
	tableMeta.condition = java_map(tableConf.condition);
	return tableMeta;
}

function java_map(map) {
	var HashMap = java.util.HashMap;
	var javaMap = HashMap();
	for (var i in map) {
		if (map.hasOwnProperty(i)) {
			javaMap.put(i, map[i]);
		}
	}
	return javaMap;
}
function java_list(list) {
	var ArrayList = java.util.ArrayList;
	var javaList = ArrayList();
	for (var i in list) {
		javaList.add(list[i]);
	}
	return javaList;
}


