


public class TGS_C {
	byte[] C_SERVER_SESSION_KEY;
	long TS4;
	Ticket_s ts;
	long Lifetime2;
	//TGS->C: Epub-c[IDs || TS4 || Tickets]
	public TGS_C(byte[] C_SERVER_SESSION_KEY, long TS4,Ticket_s ts, long Lifetime2)
	{
		this.C_SERVER_SESSION_KEY = C_SERVER_SESSION_KEY;
		this.ts = ts;
		this.TS4 = TS4;
		this.Lifetime2 = Lifetime2;
	}
}
