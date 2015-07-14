
public class Ticket_s {
    byte[] IDc, ADc;
	long TS4,Lifetime1;
	byte[] key;
	public Ticket_s(byte[] key,byte[] IDc,byte[] ADc, long TS4,long Lifetime1){
		this.key=key;
		this.IDc = IDc;
		this.ADc = ADc;
		this.TS4 = TS4;
		this.Lifetime1 = Lifetime1;
	}
	
	public byte[] getIDc()
	{
		return this.IDc;
	}
	
	public byte[] getADc()
	{
		return this.ADc;
	}
	
	public long getTS4()
	{
		return this.TS4;
	}
	
	public long getLifetime1()
	{
		return this.Lifetime1;
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}
}
