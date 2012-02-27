package mepaul.beansql;

public interface Condition {
	String toSql(TableMeta meta);
}
