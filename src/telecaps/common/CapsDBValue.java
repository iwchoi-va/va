package telecaps.common;

public class CapsDBValue {
	public static final int DB_VALUE_OID = 1;
	public static final int DB_VALUE_TRAP = 2;
	public static final int DB_VALUE_TRAP_V1 = 3;
	public static final int DB_VALUE_TRAP_V2 = 4;

	private int m_type;
	private Object m_value;

	public CapsDBValue(int type, Object value) {
		this.m_type = type;
		this.m_value = value;
	}

	public int getType() {
		return this.m_type;
	}

	public Object getValue() {
		return this.m_value;
	}

	public void setType(int type) {
		this.m_type = type;
	}

	public void setValue(Object value) {
		this.m_value = value;
	}
}
