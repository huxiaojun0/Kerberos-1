


import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
public class Kerberos {

        public static SecretKey  getKeybyPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException 
        {
            byte[] password_byte = password.getBytes();
            MessageDigest md1 = MessageDigest.getInstance("SHA-1");
            byte[] password_byte_hash = md1.digest(password_byte); //hash plaintext using SHA-1               //求明文的hash值，用SHA-1算法
            String password_hash = new String(password_byte_hash);
            DESKeySpec desKS = new DESKeySpec(password_byte_hash); 
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES"); 
            SecretKey key2 = skf.generateSecret(desKS); 
            return key2;
        }
        
        public static SecretKey getKeybybyte(byte[] abyte) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException
        {  
            String astring = new String(abyte);
			DESKeySpec desKS = new DESKeySpec(abyte); 
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES"); 
			SecretKey key2 = skf.generateSecret(desKS); 
            return key2;
            
        }
        
    	public static long getTimeStamp()
	{	
		long now = System.currentTimeMillis();
		return now;
	}
      public static boolean isInSession(long ts1,long ts2,long lifetime)
	{	
		if(ts1 + lifetime > ts2)
	            return true;
		else return false;
	}     
      
      public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException,  InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
      {      
       long  TS1,TS2,TS3,TS4,TS5; 
       String ADc = "localhost";
       String IDc = "xue";
       String password = "xue";
       byte[] IDc_byte = IDc.getBytes();
       byte[] ADc_byte = ADc.getBytes();
       long lifetime = 6000;
       String client_tgs_session_key_string = "client_tgs_session_key";
       String client_server_session_key_string = "client_server_session_key";
       String TGS_Secret_key_string = "tgs_secret_key";
  
//-----------------------------------client -----------------------------------------------------------------------------
       //calculate the hash value of password to get secret key of client                   //计算password的hash来求得secret key
       SecretKey secret_key = getKeybyPassword(password);
      //client prepare all the material(clientID+timestamp1)and send it to AS                    //client构造C_AS并发送C_AS给AS
       TS1  = getTimeStamp();
       C_AS c_as = new C_AS(IDc,TS1);
       System.out.println(secret_key);
       

//-----------------------------------   A S    --------------------------------------------------------------------------
       //AS receives ClientID and get ready to send MessageA and MessageB                                             //AS接收到c_as，并组织as-c并发送给client
       //1. Get Client_TGS_Session_Key                                                                                //1.获取Client_TGS_Session_key
       SecretKey client_tgs_session_key = getKeybybyte(client_tgs_session_key_string.getBytes());
       byte[] client_tgs_session_key_byte = client_tgs_session_key.getEncoded();
       //2. Encrypt client_TGS_session_key using secret_key of client                                                   //2.用secret_key加密client_TGS_session_key
       byte[] client_tgs_session_key_byte_encrypt = DESUtil.encrypt(secret_key, client_tgs_session_key_byte);
       //3. construct ticket_tgs                                                                                        //3.构造Ticket_tgs
       // Get secret_key of TGS and encrypt the ticket_tgs using secret_key of TGS                                                    //获得TGS的secretkey,并用TGS_Secret_key加密
       SecretKey TGS_Secret_key = getKeybybyte(TGS_Secret_key_string.getBytes());
       TS2 =  getTimeStamp();     
       byte[] IDc_use_tgs_secretkey = DESUtil.encrypt(TGS_Secret_key, IDc_byte);
       byte[] ADc_use_tgs_secretkey = DESUtil.encrypt(TGS_Secret_key, ADc_byte);
       byte[] client_tgs_session_key_use_tgs_secretgskey = DESUtil.encrypt(TGS_Secret_key, client_tgs_session_key_byte);
       Ticket_tgs ticket_tgs = new Ticket_tgs(client_tgs_session_key_use_tgs_secretgskey,IDc_byte,ADc_byte,TS2,lifetime);  
       //construct AS_C and get ready to send it                                                                                //4.构造AS_C并发送
       AS_C as_c = new AS_C(client_tgs_session_key_byte_encrypt,TS2,lifetime,ticket_tgs);
       System.out.println(TGS_Secret_key);
       System.out.println(ticket_tgs);
       



//-----------------------------------   Client    --------------------------------------------------------------------------       
       //client received MessageA and MessageB from AS                                               //cilent收到AS_C后的处理
       //1.check the validity of time                                                                 //1.验证时间的合法性
       if(!isInSession(TS1,TS2,lifetime))
       {
           System.out.println("client_as time error!");
       }
       //2. client decrypts messageA using his own secret_key to get client_tgs_session_key                       //2.client利用secret_key 解密获得Client_TGS_Session_key
       byte[] receive_client_tgs_session_key_byte_encrypt = as_c.C_TGS_SESSION_KEY;
       byte[] receive_client_tgs_session_key_byte = DESUtil.decrypt(secret_key, receive_client_tgs_session_key_byte_encrypt);
       SecretKey receive_client_tgs_session_key = getKeybybyte(receive_client_tgs_session_key_byte);
       //2. Get ready Authenticator_tgs and use receive_client_tgs_session_key to encrypt it                       //3.准备Authenticator_tgs,并用receive_client_tgs_session_key加密
       	TS3 = getTimeStamp();
       byte[] IDc_use_client_tgs_session_key = DESUtil.encrypt(receive_client_tgs_session_key, IDc_byte);
       byte[] ADc_use_client_tgs_session_key = DESUtil.encrypt(receive_client_tgs_session_key, ADc_byte);
       Authenticator_tgs authenticator_tgs = new Authenticator_tgs(IDc_use_client_tgs_session_key,ADc_use_client_tgs_session_key,TS3);     
       //4. construct C_TGS and be ready to send it                                                                  //4. 构造C_TGS并发送
       byte[] IDs = "A".getBytes();
       C_TGS c_tgs = new C_TGS(IDs,ticket_tgs,authenticator_tgs);
 
 //-----------------------------------   TGS    --------------------------------------------------------------------------    
       //TGS received MessageC and MessageD                                                                                //TGS接收到C_TGS后的处理
         //1. check the validity of time                                                                                     //1.验证时间的合法性
       if(!isInSession(ticket_tgs.getTS2(),authenticator_tgs.getTS3(),lifetime))
       {
           System.out.println("client_as time error!");
       }
       //2. decrypt ticket_tgs and get client_tgs_key                                                                       //2.解密ticket_tgs中的client_tgs_key，获得client_tgs_key
       byte[] tgs_receive_client_tgs_session_key_byte = DESUtil.decrypt(TGS_Secret_key,ticket_tgs.getKey());
       SecretKey tgs_receive_client_tgs_session_key = getKeybybyte(tgs_receive_client_tgs_session_key_byte);
       //3. decrypt MessageD(authenticator) using client_tgs_key which retrieved by the above step                              //3.用client_tgs_key解密Authenticator_tgs中的一些域，并用secret_key 加密构造ticket_s
       byte[] tgs_receive_IDc = DESUtil.encrypt(tgs_receive_client_tgs_session_key, authenticator_tgs.getIDc());
       byte[] tgs_receive_ADc = DESUtil.encrypt(tgs_receive_client_tgs_session_key, authenticator_tgs.getADc());  
       
       byte[] tgs_receive_IDc_encrypt = DESUtil.encrypt(secret_key,tgs_receive_IDc);
       byte[] tgs_receive_ADc_encrypt = DESUtil.encrypt(secret_key,tgs_receive_ADc);
       // 4.encrypt client_server_session_key using client_tgs_session_key                                                       //4.获取Client_Server_Session_key,用client_tgs_session_key加密client_session_key
       SecretKey client_server_session_key = getKeybybyte(client_server_session_key_string.getBytes());
       byte[] client_server_session_key_byte = client_server_session_key.getEncoded();

       byte[] client_server_session_key_byte_encrypt = DESUtil.encrypt(tgs_receive_client_tgs_session_key, client_server_session_key_byte);
        //construct ticket_s                                                                                                      //5.构造Ticket_s
       TS4 =  getTimeStamp();     
       byte[] client_server_session_key_use_secretgskey = DESUtil.encrypt(secret_key, client_server_session_key_byte);
       Ticket_s ticket_s = new Ticket_s(client_server_session_key_use_secretgskey,tgs_receive_IDc_encrypt,tgs_receive_ADc_encrypt,TS4,lifetime);
       //construct TGS_C and be ready to send                                                                                     //6.构造并发送TGS_C
       TGS_C tgs_c = new TGS_C(client_server_session_key_byte_encrypt,TS4,ticket_s,lifetime);

  //-----------------------------------   Client   --------------------------------------------------------------------------       
      //client received TGS_C                                                                                                     //Client接收到 TGS_C后的处理
        //1.check the validity of time                                                                                            //1.验证时间的合法性
       if(!isInSession(authenticator_tgs.getTS3(),tgs_c.TS4,lifetime))
       {
           System.out.println("client_as tiem error!");
       }
       //2. decrypt it to get client_server_key                                                                                       //2.解密获得client_server_key
       byte[] receive_client_server_session_key_byte_encrypt = tgs_c.C_SERVER_SESSION_KEY;
       byte[] receive_client_server_session_key_byte = DESUtil.decrypt(receive_client_tgs_session_key, receive_client_server_session_key_byte_encrypt);
       SecretKey receive_client_server_session_key = getKeybybyte(receive_client_server_session_key_byte);    
      //3.get ready authenticator_tgs and use receive_client_tgs_session_key to encrypt it                                          //3.准备Authenticator_tgs,并用receive_client_tgs_session_key加密
       TS5 = getTimeStamp();
       byte[] IDc_use_client_server_session_key = DESUtil.encrypt(receive_client_server_session_key, IDc_byte);
       byte[] ADc_use_client_server_session_key = DESUtil.encrypt(receive_client_server_session_key, ADc_byte);
       Authenticator_s authenticator_s = new Authenticator_s(IDc_use_client_server_session_key,ADc_use_client_server_session_key,TS5);
        //4. construct C_S and ready to send                                                                                          //4.构造C_S并发送
       C_S c_s = new C_S(ticket_s,authenticator_s);  
       
  //-----------------------------------   Server   --------------------------------------------------------------------------        
       //server received C_S                                                                                                         //server接收到C_S
       //1.server decrypts ticket_s using its own secret key to retrieve client_server_key                                            //1.解密ticket_s中的client_server_key，获得client_server_key
       byte[] tgs_receive_client_server_session_key_byte = DESUtil.decrypt(secret_key,c_s.getTicket_s().getKey());
       SecretKey server_receive_client_server_session_key = getKeybybyte(tgs_receive_client_server_session_key_byte); 
       //2.decrypt authenticator_tgs using client_server_session_key to get timestamp and plus it 1, then encrypt it using client_server_key to get a new timestamp and send it to client. //2.用client_server_key解密Authenticator_tgs获得timestamp，并加1,用client_server_key加密timestamp发送给client
       long timestamp_long = authenticator_s.getTS5() + 1;
        //convert it into byte[]                                                                                                                   //转换成byte[]
       byte[] timestamp_byte = Long.toString(timestamp_long).getBytes();
       byte[] timestamp_byte_encrypt = DESUtil.encrypt(server_receive_client_server_session_key, timestamp_byte);

  //-----------------------------------   Client   --------------------------------------------------------------------------   
      //1. client received and decrypt to get client_receive_timestamp                                                                   //1.client收到后，用client_server_key 解密timestamp_byte_encrypt，
       byte[] client_receive_timestamp_byte = DESUtil.decrypt(server_receive_client_server_session_key, timestamp_byte_encrypt);
       //convert byte [] into long type                                                                                                //把byte类型转换成long类型
       long receive_timestamp_long = Long.parseLong(new String(client_receive_timestamp_byte));
       //2. check timestamp updated or not !!!                                                                                              //2.判断timestamp是否被更新了
       if(receive_timestamp_long == authenticator_s.getTS5() + 1)
       {
           System.out.println("timestamp has been updated!");
       }
       
      }

       
       
       
    

}
