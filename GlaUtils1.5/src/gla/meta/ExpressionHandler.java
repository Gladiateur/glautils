/*
 * ExpressionHandler.java 18/06/28
 */
package gla.meta;

/**
 * ExpressionHandler
 * 
 * @author Gladiateur
 * @since 1.4
 */
public class ExpressionHandler implements ConfigHandler {
	private String tables;
	private static ConditionBean conditionBean = EntityWriter.getConditionBean();
	
	public ExpressionHandler(String tables) {
		this.tables = tables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gla.meta.ConfigHandler#doConfig()
	 */
	@Override
	public void doConfig() { 
		String t = tables.trim();
		conditionBean.setKeyTablesFlag(( "*".equals(t)) ? true : false);
		conditionBean.setKeyTablesExclusive('!' == t.charAt(0));
		EntityWriter.setConditionBean(conditionBean);
	}

}
