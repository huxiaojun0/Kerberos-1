
public class Ticket_tgs {
    byte[] IDc, ADc;
	long TS2,Lifetime1;
	byte[] key;
	public Ticket_tgs(byte[] key,byte[] IDc,byte[] ADc, long TS2,long Lifetime1){
		this.key=key;
		this.IDc = IDc;
		this.ADc = ADc;
		this.TS2 = TS2;
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
	
	public long getTS2()
	{
		return this.TS2;
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
