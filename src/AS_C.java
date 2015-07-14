
public class AS_C {
	byte[] C_TGS_SESSION_KEY;
	Ticket_tgs Tickettgs;
	long TS2,Lifetime1;
	public AS_C(byte[] C_TGS_SESSION_KEY,long TS2,long Lifetime1,Ticket_tgs Tickettgs )
	{
		this.C_TGS_SESSION_KEY = C_TGS_SESSION_KEY;
		this.Tickettgs = Tickettgs;
		this.TS2 = TS2;
		this.Lifetime1 = Lifetime1;
	}
}
