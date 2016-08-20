package wallet.agents.VM.instructions.sys_calls;

import java.sql.ResultSet;
import java.util.ArrayList;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.UTILS;

public class CAPI extends CInstruction
{
    // Dest
    CToken dest;
    
    // Table
    CToken table;
    
    // Column
    CToken col;
    
    // Type
    CToken type;
    
    // Val
    CToken val;
    
    // Min
    CToken min;
    
    // Max
    CToken max;
    
    // Tables
    ArrayList tabs=new ArrayList();
    
    // Address
    ArrayList fields_adr=new ArrayList();
    
    public CAPI(VM VM, ArrayList<CToken>tokens) 
    {
        // Constructor
        super(VM, "API");
        
        // Dest
        this.dest=tokens.get(1);
        
        // Table
        this.table=tokens.get(3);
        
        // Col
        this.col=tokens.get(5);
        
        // Type
        this.type=tokens.get(7);
    
        // Val
        this.val=tokens.get(9);
        
        // Min
        this.min=tokens.get(11);
        
        // Max
        this.max=tokens.get(13);
        
        // Tables
        this.tabs.add("adr");
        this.tabs.add("ads");
        this.tabs.add("agents");
        this.tabs.add("assets");
        this.tabs.add("assets_owners");
        this.tabs.add("assets_mkts");
        this.tabs.add("assets_mkts_pos");
        this.tabs.add("domains");
        this.tabs.add("escrowed");
        this.tabs.add("feeds");
        this.tabs.add("feeds_branches");
        this.tabs.add("feeds_bets");
        this.tabs.add("feeds_bets_pos");
        this.tabs.add("feeds_spec_mkts");
        this.tabs.add("feeds_spec_mkts_pos");
        this.tabs.add("tweets");
        this.tabs.add("upvotes");
        this.tabs.add("tweets_follow");
        this.tabs.add("comments");
        this.tabs.add("profiles");
        
        // Adr fields
        this.fields_adr.add("adr");
        this.fields_adr.add("balance");
        this.fields_adr.add("created");
        this.fields_adr.add("block");
        this.fields_adr.add("sealed");
        this.fields_adr.add("rowhash");
        this.fields_adr.add("last_interest");
        
        // Ads  
        this.fields_adr.add("country");
        this.fields_adr.add("adr");
        this.fields_adr.add("title"); 
        this.fields_adr.add("message"); 
        this.fields_adr.add("link");
        this.fields_adr.add("mkt_bid");
        this.fields_adr.add("expire");
        this.fields_adr.add("block");
        this.fields_adr.add("rowhash");
        
        // Agents
        this.fields_adr.add("adr");
        this.fields_adr.add("owner");
        this.fields_adr.add("name");
        this.fields_adr.add("description");
        this.fields_adr.add("pay_adr");
        this.fields_adr.add("website");
        this.fields_adr.add("pic");
        this.fields_adr.add("globals");
        this.fields_adr.add("signals");
        this.fields_adr.add("interface");
        this.fields_adr.add("code");
        this.fields_adr.add("status");
        this.fields_adr.add("exec_log");
        this.fields_adr.add("categ");
        this.fields_adr.add("ver");
        this.fields_adr.add("run_period");
        this.fields_adr.add("sealed"); 
        this.fields_adr.add("price");
        this.fields_adr.add("storage");
        this.fields_adr.add("expire");
        this.fields_adr.add("aID");
        this.fields_adr.add("dir");
        this.fields_adr.add("block");
        this.fields_adr.add("rowhash");
        
        // Assets
        this.fields_adr.add("adr");
        this.fields_adr.add("symbol");
        this.fields_adr.add("title");
        this.fields_adr.add("description");
        this.fields_adr.add("how_buy");
        this.fields_adr.add("how_sell");
        this.fields_adr.add("web_page");
        this.fields_adr.add("pic");
        this.fields_adr.add("expire");
        this.fields_adr.add("qty");
        this.fields_adr.add("trans_fee_adr");
        this.fields_adr.add("trans_fee");
        this.fields_adr.add("linked_mktID");
        this.fields_adr.add("rowhash");
        this.fields_adr.add("block");

        // Assets_owners
        this.fields_adr.add("owner");
        this.fields_adr.add("symbol");  
        this.fields_adr.add("qty");
        this.fields_adr.add("invested");
        this.fields_adr.add("rowhash"); 
        this.fields_adr.add("block");
        
        // Assets_mkts
        this.fields_adr.add("adr");
        this.fields_adr.add("asset");
        this.fields_adr.add("cur");
        this.fields_adr.add("name");
        this.fields_adr.add("description");
        this.fields_adr.add("decimals"); 
        this.fields_adr.add("block");
        this.fields_adr.add("expire");
        this.fields_adr.add("last_price");
        this.fields_adr.add("ask");
        this.fields_adr.add("bid");
        this.fields_adr.add("rowhash");
        this.fields_adr.add("mktID");

        // Assets_mkts_pos
        this.fields_adr.add("adr");
        this.fields_adr.add("mktID");
        this.fields_adr.add("tip");
        this.fields_adr.add("qty");
        this.fields_adr.add("price");
        this.fields_adr.add("block");
        this.fields_adr.add("orderID");
        this.fields_adr.add("rowhash");
        this.fields_adr.add("expire");
        
        // Escrowed
        this.fields_adr.add("trans_hash"); 
        this.fields_adr.add("sender_adr");
        this.fields_adr.add("rec_adr");
        this.fields_adr.add("escrower");
        this.fields_adr.add("amount");
        this.fields_adr.add("cur");
        this.fields_adr.add("block");
        this.fields_adr.add("rowhash");
        
        // Domains
        this.fields_adr.add("adr");
        this.fields_adr.add("domain");
        this.fields_adr.add("expire"); 
        this.fields_adr.add("sale_price");
        this.fields_adr.add("block");
        this.fields_adr.add("rowhash");
        
        // Blocks
        this.fields_adr.add("hash");
        this.fields_adr.add("block");
        this.fields_adr.add("prev_hash");
        this.fields_adr.add("signer");
        this.fields_adr.add("packets");
        this.fields_adr.add("tstamp");
        this.fields_adr.add("nonce");
        this.fields_adr.add("size"); 
        this.fields_adr.add("net_dif");
        this.fields_adr.add("commited");
        this.fields_adr.add("confirmations");
        this.fields_adr.add("payload_hash");
        this.fields_adr.add("tab_1");
        this.fields_adr.add("tab_2");
        this.fields_adr.add("tab_3");
        this.fields_adr.add("tab_4");
        this.fields_adr.add("tab_5");
        this.fields_adr.add("tab_6");
        this.fields_adr.add("tab_7");
        this.fields_adr.add("tab_8");
        this.fields_adr.add("tab_9");
        this.fields_adr.add("tab_10");
        this.fields_adr.add("tab_11");
        this.fields_adr.add("tab_12");
        this.fields_adr.add("tab_13");   
        this.fields_adr.add("tab_14");
        this.fields_adr.add("tab_15");
        this.fields_adr.add("signer_balance");

        // Feeds
        this.fields_adr.add("adr");
        this.fields_adr.add("name");
        this.fields_adr.add("description");
        this.fields_adr.add("website");
        this.fields_adr.add("symbol");
        this.fields_adr.add("expire");
        this.fields_adr.add("branches");
        this.fields_adr.add("rowhash");
        this.fields_adr.add("block");

        // Feeds_branches 
        this.fields_adr.add("feed_symbol");
        this.fields_adr.add("symbol");
        this.fields_adr.add("name");
        this.fields_adr.add("description");
        this.fields_adr.add("type");
        this.fields_adr.add("rl_symbol");
        this.fields_adr.add("fee"); 
        this.fields_adr.add("expire");
        this.fields_adr.add("val");
        this.fields_adr.add("mkt_status");
        this.fields_adr.add("rowhash");  
        this.fields_adr.add("block");

        // Feeds_bets
        this.fields_adr.add("mktID");
        this.fields_adr.add("adr");
        this.fields_adr.add("feed_1");
        this.fields_adr.add("branch_1");
        this.fields_adr.add("feed_2");
        this.fields_adr.add("branch_2");
        this.fields_adr.add("feed_3");
        this.fields_adr.add("branch_3");
        this.fields_adr.add("last_price");
        this.fields_adr.add("tip");
        this.fields_adr.add("val_1");
        this.fields_adr.add("val_2");
        this.fields_adr.add("title");
        this.fields_adr.add("description");
        this.fields_adr.add("budget");
        this.fields_adr.add("win_multiplier");
        this.fields_adr.add("start_block");
        this.fields_adr.add("end_block");
        this.fields_adr.add("accept_block");
        this.fields_adr.add("cur");
        this.fields_adr.add("bets");
        this.fields_adr.add("invested");
        this.fields_adr.add("status");  
        this.fields_adr.add("rowhash");
        this.fields_adr.add("block");

        // Feeds_bets_pos
        this.fields_adr.add("betID");
        this.fields_adr.add("adr");
        this.fields_adr.add("amount");
        this.fields_adr.add("block"); 
        this.fields_adr.add("rowhash");

        // Feeds_spec_mkts
        this.fields_adr.add("adr");
        this.fields_adr.add("feed_1");
        this.fields_adr.add("branch_1");
        this.fields_adr.add("feed_2");
        this.fields_adr.add("branch_2");
        this.fields_adr.add("feed_3");
        this.fields_adr.add("branch_3");
        this.fields_adr.add("last_price");
        this.fields_adr.add("cur");
        this.fields_adr.add("max_leverage");
        this.fields_adr.add("spread");
        this.fields_adr.add("real_symbol");
        this.fields_adr.add("pos_type");
        this.fields_adr.add("title");
        this.fields_adr.add("description");
        this.fields_adr.add("max_margin");  
        this.fields_adr.add("expire");
        this.fields_adr.add("block");
        this.fields_adr.add("status");
        this.fields_adr.add("rowhash");
        this.fields_adr.add("mktID");

        // Feeds_spec_mkts_pos
        this.fields_adr.add("mktID");
        this.fields_adr.add("posID");
        this.fields_adr.add("adr");
        this.fields_adr.add("open");
        this.fields_adr.add("sl");
        this.fields_adr.add("tp");
        this.fields_adr.add("leverage");
        this.fields_adr.add("qty");
        this.fields_adr.add("status");
        this.fields_adr.add("open_line");
        this.fields_adr.add("tip");
        this.fields_adr.add("pl");
        this.fields_adr.add("spread");
        this.fields_adr.add("margin");
        this.fields_adr.add("close_reason");
        this.fields_adr.add("closed_pl");
        this.fields_adr.add("closed_margin");
        this.fields_adr.add("block");
        this.fields_adr.add("last_block");
        this.fields_adr.add("rowhash");

        // packets
        this.fields_adr.add("packet_hash");
        this.fields_adr.add("par_1_name");
        this.fields_adr.add("par_1_val");
        this.fields_adr.add("par_2_name");
        this.fields_adr.add("par_2_val");
        this.fields_adr.add("par_3_name");
        this.fields_adr.add("par_3_val");
        this.fields_adr.add("par_4_name");
        this.fields_adr.add("par_4_val");
        this.fields_adr.add("par_5_name");
        this.fields_adr.add("par_5_val"); 
        this.fields_adr.add("block");
        this.fields_adr.add("tstamp");
        this.fields_adr.add("confirms");
        this.fields_adr.add("block_hash");
        this.fields_adr.add("payload_hash");
        this.fields_adr.add("payload_size");
        this.fields_adr.add("packet_type");
        this.fields_adr.add("fee_src");
        this.fields_adr.add("fee_amount");
        this.fields_adr.add("fee_hash");
        
        // Tweets
        this.fields_adr.add("tweetID");
        this.fields_adr.add("adr"); 
        this.fields_adr.add("mes"); 
        this.fields_adr.add("pic_1");
        this.fields_adr.add("pic_2");
        this.fields_adr.add("pic_3");
        this.fields_adr.add("pic_4");
        this.fields_adr.add("pic_5");
        this.fields_adr.add("video");
        this.fields_adr.add("rowhash");
        this.fields_adr.add("block");
        this.fields_adr.add("retweet");
        this.fields_adr.add("retweet_tweet_ID");
        this.fields_adr.add("likes");
        this.fields_adr.add("comments");
        this.fields_adr.add("retweets");

        // Tweets_follow
        this.fields_adr.add("adr");
        this.fields_adr.add("follows");
        this.fields_adr.add("expire");
        this.fields_adr.add("block");
        this.fields_adr.add("rowhash");
 
        // upvotes
        this.fields_adr.add("tweetID");
        this.fields_adr.add("adr");
        this.fields_adr.add("block");
        this.fields_adr.add("rowhash");

        // comments
        this.fields_adr.add("adr");
        this.fields_adr.add("parent_type");
        this.fields_adr.add("parentID");
        this.fields_adr.add("comID");
        this.fields_adr.add("mes");
        this.fields_adr.add("status");
        this.fields_adr.add("rowhash");
        this.fields_adr.add("block");
    }
    
    public boolean colExist(ArrayList cols, String col) throws Exception
    {
        // Column exist ?
        for (int a=0; a<=cols.size()-1; a++)
            if (cols.get(a).equals(col))
                return true;
        
        // No
        return false;
    }
    
    public boolean colValid(String table, String col) throws Exception
    {
       // Adr
       if (table.equals("adr") && !this.colExist(fields_adr, col)) 
           return false;
       
       // Return
       return true;
    }
    
    public boolean tableValid(String tab) throws Exception
    {
       if (!this.colExist(this.tabs, tab))   
           return false;
       else
           return true;
    }
    
    public ResultSet query() throws Exception
    {
        if (this.type.cel.val.equals("exact"))
            return UTILS.DB.executeQuery("SELECT * "
                                         + "FROM "+this.table.cel.val+" "
                                        + "WHERE "+this.col.cel.val+"='"+this.val.cel.val+"'");
            
    
        else
            return UTILS.DB.executeQuery("SELECT * "
                                         + "FROM "+this.table.cel.val+" "
                                        + "WHERE "+this.col.cel.val+">="+this.min.cel.val
                                          +" AND "+this.col.cel.val+"<="+this.max.cel.val);
    }
    
    public CCell loadAdr() throws Exception
    {
        // Result
        ResultSet rs=this.query();
        
        // Creates new cell
        CCell c=new CCell("");
        
        // Adr
        CCell colAdr=new CCell("");
        colAdr.name="adr";
        
        // Balance
        CCell colBalance=new CCell(0);
        colBalance.name="balance";
        
        // Created
        CCell colCreated=new CCell(0);
        colCreated.name="created";
        
        // Sealed
        CCell colSealed=new CCell(0);
        colSealed.name="sealed";
        
        // Last interest
        CCell colLastInt=new CCell(0);
        colLastInt.name="last_interest";
        
        // Block
        CCell colBlock=new CCell(0);
        colBlock.name="block";
        
        // Rowhash
        CCell colRowhash=new CCell("");
        colRowhash.name="rowhash";
           
        // Has data
        if (UTILS.DB.hasData(rs))
        {
           // Load data
           while (rs.next())
           {
               // Adr
               colAdr.addCell(new CCell(rs.getString("adr")));
               
               // Balance
               colBalance.addCell(new CCell(rs.getDouble("balance")));
               
               // Created
               colCreated.addCell(new CCell(rs.getLong("created")));
               
               // Sealed
               colSealed.addCell(new CCell(rs.getLong("sealed")));
               
               // Last Interest
               colLastInt.addCell(new CCell(rs.getString("last_interest")));
               
               // Block
               colBlock.addCell(new CCell(rs.getString("block")));
               
               // Rowhash
               colRowhash.addCell(new CCell(rs.getString("rowhash")));
           }
        }
        else 
        {
            c.copy(new CCell(0));
            return c;
        }  
        
        // Adr
        c.addCell(colAdr);
        
        // Balance
        c.addCell(colBalance);
        
        // Created
        c.addCell(colCreated);
        
        // Sealed
        c.addCell(colSealed);
        
        // Last interest
        c.addCell(colLastInt);
        
        // Block
        c.addCell(colBlock);
        
        // Rowhash
        c.addCell(colRowhash);
        
        // Copy
        return c;
    }
    
   
    public CCell loadAds() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Country
       CCell colCountry=new CCell("");
       colCountry.name="country";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Title
       CCell colTitle=new CCell("");
       colTitle.name="title";

       // Message
       CCell colMessage=new CCell("");
       colMessage.name="message";

       // Link
       CCell colLink=new CCell("");
       colLink.name="link";

       // Mkt_bid
       CCell colMkt_bid=new CCell("");
       colMkt_bid.name="mkt_bid";

       // Expire
       CCell colExpire=new CCell("");
       colExpire.name="expire";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Country
                     colCountry.addCell(new CCell(rs.getString("country")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Title
                     colTitle.addCell(new CCell(rs.getString("title")));

                     // Message
                     colMessage.addCell(new CCell(rs.getString("message")));

                     // Link
                     colLink.addCell(new CCell(rs.getString("link")));

                     // Mkt_bid
                     colMkt_bid.addCell(new CCell(rs.getString("mkt_bid")));

                     // Expire
                     colExpire.addCell(new CCell(rs.getString("expire")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Country
       c.addCell(colCountry);

       // Adr
       c.addCell(colAdr);

       // Title
       c.addCell(colTitle);

       // Message
       c.addCell(colMessage);

       // Link
       c.addCell(colLink);

       // Mkt_bid
       c.addCell(colMkt_bid);

       // Expire
       c.addCell(colExpire);

       // Block
       c.addCell(colBlock);

       // Rowhash
       c.addCell(colRowhash);

       // Copy
       return c;
    }

public CCell loadAssets() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Symbol
       CCell colSymbol=new CCell("");
       colSymbol.name="symbol";

       // Title
       CCell colTitle=new CCell("");
       colTitle.name="title";

       // Description
       CCell colDescription=new CCell("");
       colDescription.name="description";

       // How_buy
       CCell colHow_buy=new CCell("");
       colHow_buy.name="how_buy";

       // How_sell
       CCell colHow_sell=new CCell("");
       colHow_sell.name="how_sell";

       // Web_page
       CCell colWeb_page=new CCell("");
       colWeb_page.name="web_page";

       // Pic
       CCell colPic=new CCell("");
       colPic.name="pic";

       // Expire
       CCell colExpire=new CCell("");
       colExpire.name="expire";

       // Qty
       CCell colQty=new CCell("");
       colQty.name="qty";

       // Trans_fee_adr
       CCell colTrans_fee_adr=new CCell("");
       colTrans_fee_adr.name="trans_fee_adr";

       // Trans_fee
       CCell colTrans_fee=new CCell("");
       colTrans_fee.name="trans_fee";

       // Linked_mktID
       CCell colLinked_mktID=new CCell("");
       colLinked_mktID.name="linked_mktID";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Symbol
                     colSymbol.addCell(new CCell(rs.getString("symbol")));

                     // Title
                     colTitle.addCell(new CCell(rs.getString("title")));

                     // Description
                     colDescription.addCell(new CCell(rs.getString("description")));

                     // How_buy
                     colHow_buy.addCell(new CCell(rs.getString("how_buy")));

                     // How_sell
                     colHow_sell.addCell(new CCell(rs.getString("how_sell")));

                     // Web_page
                     colWeb_page.addCell(new CCell(rs.getString("web_page")));

                     // Pic
                     colPic.addCell(new CCell(rs.getString("pic")));

                     // Expire
                     colExpire.addCell(new CCell(rs.getString("expire")));

                     // Qty
                     colQty.addCell(new CCell(rs.getString("qty")));

                     // Trans_fee_adr
                     colTrans_fee_adr.addCell(new CCell(rs.getString("trans_fee_adr")));

                     // Trans_fee
                     colTrans_fee.addCell(new CCell(rs.getString("trans_fee")));

                     // Linked_mktID
                     colLinked_mktID.addCell(new CCell(rs.getString("linked_mktID")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Adr
       c.addCell(colAdr);

       // Symbol
       c.addCell(colSymbol);

       // Title
       c.addCell(colTitle);

       // Description
       c.addCell(colDescription);

       // How_buy
       c.addCell(colHow_buy);

       // How_sell
       c.addCell(colHow_sell);

       // Web_page
       c.addCell(colWeb_page);

       // Pic
       c.addCell(colPic);

       // Expire
       c.addCell(colExpire);

       // Qty
       c.addCell(colQty);

       // Trans_fee_adr
       c.addCell(colTrans_fee_adr);

       // Trans_fee
       c.addCell(colTrans_fee);

       // Linked_mktID
       c.addCell(colLinked_mktID);

       // Rowhash
       c.addCell(colRowhash);

       // Block
       c.addCell(colBlock);

       // Copy
       return c;
    }

    public CCell loadAgents() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Owner
       CCell colOwner=new CCell("");
       colOwner.name="owner";

       // Name
       CCell colName=new CCell("");
       colName.name="name";

       // Description
       CCell colDescription=new CCell("");
       colDescription.name="description";

       // Pay_adr
       CCell colPay_adr=new CCell("");
       colPay_adr.name="pay_adr";

       // Website
       CCell colWebsite=new CCell("");
       colWebsite.name="website";

       // Pic
       CCell colPic=new CCell("");
       colPic.name="pic";

       // Globals
       CCell colGlobals=new CCell("");
       colGlobals.name="globals";

       // Signals
       CCell colSignals=new CCell("");
       colSignals.name="signals";

       // Interface
       CCell colInterface=new CCell("");
       colInterface.name="interface";

       // Code
       CCell colCode=new CCell("");
       colCode.name="code";

       // Status
       CCell colStatus=new CCell("");
       colStatus.name="status";

       // Exec_log
       CCell colExec_log=new CCell("");
       colExec_log.name="exec_log";

       // Categ
       CCell colCateg=new CCell("");
       colCateg.name="categ";

       // Ver
       CCell colVer=new CCell("");
       colVer.name="ver";

       // Run_period
       CCell colRun_period=new CCell("");
       colRun_period.name="run_period";

       // Sealed
       CCell colSealed=new CCell("");
       colSealed.name="sealed";

       // Price
       CCell colPrice=new CCell("");
       colPrice.name="price";

       // Storage
       CCell colStorage=new CCell("");
       colStorage.name="storage";

       // Expire
       CCell colExpire=new CCell("");
       colExpire.name="expire";

       // AID
       CCell colAID=new CCell("");
       colAID.name="aID";

       // Dir
       CCell colDir=new CCell("");
       colDir.name="dir";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Owner
                     colOwner.addCell(new CCell(rs.getString("owner")));

                     // Name
                     colName.addCell(new CCell(rs.getString("name")));

                     // Description
                     colDescription.addCell(new CCell(rs.getString("description")));

                     // Pay_adr
                     colPay_adr.addCell(new CCell(rs.getString("pay_adr")));

                     // Website
                     colWebsite.addCell(new CCell(rs.getString("website")));

                     // Pic
                     colPic.addCell(new CCell(rs.getString("pic")));

                     // Globals
                     colGlobals.addCell(new CCell(rs.getString("globals")));

                     // Signals
                     colSignals.addCell(new CCell(rs.getString("signals")));

                     // Interface
                     colInterface.addCell(new CCell(rs.getString("interface")));

                     // Code
                     colCode.addCell(new CCell(rs.getString("code")));

                     // Status
                     colStatus.addCell(new CCell(rs.getString("status")));

                     // Exec_log
                     colExec_log.addCell(new CCell(rs.getString("exec_log")));

                     // Categ
                     colCateg.addCell(new CCell(rs.getString("categ")));

                     // Ver
                     colVer.addCell(new CCell(rs.getString("ver")));

                     // Run_period
                     colRun_period.addCell(new CCell(rs.getString("run_period")));

                     // Sealed
                     colSealed.addCell(new CCell(rs.getString("sealed")));

                     // Price
                     colPrice.addCell(new CCell(rs.getString("price")));

                     // Storage
                     colStorage.addCell(new CCell(rs.getString("storage")));

                     // Expire
                     colExpire.addCell(new CCell(rs.getString("expire")));

                     // AID
                     colAID.addCell(new CCell(rs.getString("aID")));

                     // Dir
                     colDir.addCell(new CCell(rs.getString("dir")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Adr
       c.addCell(colAdr);

       // Owner
       c.addCell(colOwner);

       // Name
       c.addCell(colName);

       // Description
       c.addCell(colDescription);

       // Pay_adr
       c.addCell(colPay_adr);

       // Website
       c.addCell(colWebsite);

       // Pic
       c.addCell(colPic);

       // Globals
       c.addCell(colGlobals);

       // Signals
       c.addCell(colSignals);

       // Interface
       c.addCell(colInterface);

       // Code
       c.addCell(colCode);

       // Status
       c.addCell(colStatus);

       // Exec_log
       c.addCell(colExec_log);

       // Categ
       c.addCell(colCateg);

       // Ver
       c.addCell(colVer);

       // Run_period
       c.addCell(colRun_period);

       // Sealed
       c.addCell(colSealed);

       // Price
       c.addCell(colPrice);

       // Storage
       c.addCell(colStorage);

       // Expire
       c.addCell(colExpire);

       // AID
       c.addCell(colAID);

       // Dir
       c.addCell(colDir);

       // Block
       c.addCell(colBlock);

       // Rowhash
       c.addCell(colRowhash);

       // Copy
       return c;
    }

    public CCell loadAssetsOwners() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Owner
       CCell colOwner=new CCell("");
       colOwner.name="owner";

       // Symbol
       CCell colSymbol=new CCell("");
       colSymbol.name="symbol";

       // Qty
       CCell colQty=new CCell("");
       colQty.name="qty";

       // Invested
       CCell colInvested=new CCell("");
       colInvested.name="invested";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Owner
                     colOwner.addCell(new CCell(rs.getString("owner")));

                     // Symbol
                     colSymbol.addCell(new CCell(rs.getString("symbol")));

                     // Qty
                     colQty.addCell(new CCell(rs.getString("qty")));

                     // Invested
                     colInvested.addCell(new CCell(rs.getString("invested")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Owner
       c.addCell(colOwner);

       // Symbol
       c.addCell(colSymbol);

       // Qty
       c.addCell(colQty);

       // Invested
       c.addCell(colInvested);

       // Rowhash
       c.addCell(colRowhash);

       // Block
       c.addCell(colBlock);

       // Copy
       return c;
    }

    public CCell loadAssetsMkts() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Asset
       CCell colAsset=new CCell("");
       colAsset.name="asset";

       // Cur
       CCell colCur=new CCell("");
       colCur.name="cur";

       // Name
       CCell colName=new CCell("");
       colName.name="name";

       // Description
       CCell colDescription=new CCell("");
       colDescription.name="description";

       // Decimals
       CCell colDecimals=new CCell("");
       colDecimals.name="decimals";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Expire
       CCell colExpire=new CCell("");
       colExpire.name="expire";

       // Last_price
       CCell colLast_price=new CCell("");
       colLast_price.name="last_price";

       // Ask
       CCell colAsk=new CCell("");
       colAsk.name="ask";

       // Bid
       CCell colBid=new CCell("");
       colBid.name="bid";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // MktID
       CCell colMktID=new CCell("");
       colMktID.name="mktID";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Asset
                     colAsset.addCell(new CCell(rs.getString("asset")));

                     // Cur
                     colCur.addCell(new CCell(rs.getString("cur")));

                     // Name
                     colName.addCell(new CCell(rs.getString("name")));

                     // Description
                     colDescription.addCell(new CCell(rs.getString("description")));

                     // Decimals
                     colDecimals.addCell(new CCell(rs.getString("decimals")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Expire
                     colExpire.addCell(new CCell(rs.getString("expire")));

                     // Last_price
                     colLast_price.addCell(new CCell(rs.getString("last_price")));

                     // Ask
                     colAsk.addCell(new CCell(rs.getString("ask")));

                     // Bid
                     colBid.addCell(new CCell(rs.getString("bid")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

                     // MktID
                     colMktID.addCell(new CCell(rs.getString("mktID")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Adr
       c.addCell(colAdr);

       // Asset
       c.addCell(colAsset);

       // Cur
       c.addCell(colCur);

       // Name
       c.addCell(colName);

       // Description
       c.addCell(colDescription);

       // Decimals
       c.addCell(colDecimals);

       // Block
       c.addCell(colBlock);

       // Expire
       c.addCell(colExpire);

       // Last_price
       c.addCell(colLast_price);

       // Ask
       c.addCell(colAsk);

       // Bid
       c.addCell(colBid);

       // Rowhash
       c.addCell(colRowhash);

       // MktID
       c.addCell(colMktID);

       // Copy
       return c;
    }

    public CCell loadAssetsMktsPos() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // MktID
       CCell colMktID=new CCell("");
       colMktID.name="mktID";

       // Tip
       CCell colTip=new CCell("");
       colTip.name="tip";

       // Qty
       CCell colQty=new CCell("");
       colQty.name="qty";

       // Price
       CCell colPrice=new CCell("");
       colPrice.name="price";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // OrderID
       CCell colOrderID=new CCell("");
       colOrderID.name="orderID";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Expire
       CCell colExpire=new CCell("");
       colExpire.name="expire";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // MktID
                     colMktID.addCell(new CCell(rs.getString("mktID")));

                     // Tip
                     colTip.addCell(new CCell(rs.getString("tip")));

                     // Qty
                     colQty.addCell(new CCell(rs.getString("qty")));

                     // Price
                     colPrice.addCell(new CCell(rs.getString("price")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // OrderID
                     colOrderID.addCell(new CCell(rs.getString("orderID")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

                     // Expire
                     colExpire.addCell(new CCell(rs.getString("expire")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Adr
       c.addCell(colAdr);

       // MktID
       c.addCell(colMktID);

       // Tip
       c.addCell(colTip);

       // Qty
       c.addCell(colQty);

       // Price
       c.addCell(colPrice);

       // Block
       c.addCell(colBlock);

       // OrderID
       c.addCell(colOrderID);

       // Rowhash
       c.addCell(colRowhash);

       // Expire
       c.addCell(colExpire);

       // Copy
       return c;
    }

    public CCell loadBlocks() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Hash
       CCell colHash=new CCell("");
       colHash.name="hash";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Prev_hash
       CCell colPrev_hash=new CCell("");
       colPrev_hash.name="prev_hash";

       // Signer
       CCell colSigner=new CCell("");
       colSigner.name="signer";

       // Packets
       CCell colPackets=new CCell("");
       colPackets.name="packets";

       // Tstamp
       CCell colTstamp=new CCell("");
       colTstamp.name="tstamp";

       // Nonce
       CCell colNonce=new CCell("");
       colNonce.name="nonce";

       // Size
       CCell colSize=new CCell("");
       colSize.name="size";

       // Net_dif
       CCell colNet_dif=new CCell("");
       colNet_dif.name="net_dif";

       // Commited
       CCell colCommited=new CCell("");
       colCommited.name="commited";

       // Confirmations
       CCell colConfirmations=new CCell("");
       colConfirmations.name="confirmations";

       // Payload_hash
       CCell colPayload_hash=new CCell("");
       colPayload_hash.name="payload_hash";

       // Tab_1
       CCell colTab_1=new CCell("");
       colTab_1.name="tab_1";

       // Tab_2
       CCell colTab_2=new CCell("");
       colTab_2.name="tab_2";

       // Tab_3
       CCell colTab_3=new CCell("");
       colTab_3.name="tab_3";

       // Tab_4
       CCell colTab_4=new CCell("");
       colTab_4.name="tab_4";

       // Tab_5
       CCell colTab_5=new CCell("");
       colTab_5.name="tab_5";

       // Tab_6
       CCell colTab_6=new CCell("");
       colTab_6.name="tab_6";

       // Tab_7
       CCell colTab_7=new CCell("");
       colTab_7.name="tab_7";

       // Tab_8
       CCell colTab_8=new CCell("");
       colTab_8.name="tab_8";

       // Tab_9
       CCell colTab_9=new CCell("");
       colTab_9.name="tab_9";

       // Tab_10
       CCell colTab_10=new CCell("");
       colTab_10.name="tab_10";

       // Tab_11
       CCell colTab_11=new CCell("");
       colTab_11.name="tab_11";

       // Tab_12
       CCell colTab_12=new CCell("");
       colTab_12.name="tab_12";

       // Tab_13
       CCell colTab_13=new CCell("");
       colTab_13.name="tab_13";

       // Tab_14
       CCell colTab_14=new CCell("");
       colTab_14.name="tab_14";

       // Tab_15
       CCell colTab_15=new CCell("");
       colTab_15.name="tab_15";

       // Signer_balance
       CCell colSigner_balance=new CCell("");
       colSigner_balance.name="signer_balance";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Hash
                     colHash.addCell(new CCell(rs.getString("hash")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Prev_hash
                     colPrev_hash.addCell(new CCell(rs.getString("prev_hash")));

                     // Signer
                     colSigner.addCell(new CCell(rs.getString("signer")));

                     // Packets
                     colPackets.addCell(new CCell(rs.getString("packets")));

                     // Tstamp
                     colTstamp.addCell(new CCell(rs.getString("tstamp")));

                     // Nonce
                     colNonce.addCell(new CCell(rs.getString("nonce")));

                     // Size
                     colSize.addCell(new CCell(rs.getString("size")));

                     // Net_dif
                     colNet_dif.addCell(new CCell(rs.getString("net_dif")));

                     // Commited
                     colCommited.addCell(new CCell(rs.getString("commited")));

                     // Confirmations
                     colConfirmations.addCell(new CCell(rs.getString("confirmations")));

                     // Payload_hash
                     colPayload_hash.addCell(new CCell(rs.getString("payload_hash")));

                     // Tab_1
                     colTab_1.addCell(new CCell(rs.getString("tab_1")));

                     // Tab_2
                     colTab_2.addCell(new CCell(rs.getString("tab_2")));

                     // Tab_3
                     colTab_3.addCell(new CCell(rs.getString("tab_3")));

                     // Tab_4
                     colTab_4.addCell(new CCell(rs.getString("tab_4")));

                     // Tab_5
                     colTab_5.addCell(new CCell(rs.getString("tab_5")));

                     // Tab_6
                     colTab_6.addCell(new CCell(rs.getString("tab_6")));

                     // Tab_7
                     colTab_7.addCell(new CCell(rs.getString("tab_7")));

                     // Tab_8
                     colTab_8.addCell(new CCell(rs.getString("tab_8")));

                     // Tab_9
                     colTab_9.addCell(new CCell(rs.getString("tab_9")));

                     // Tab_10
                     colTab_10.addCell(new CCell(rs.getString("tab_10")));

                     // Tab_11
                     colTab_11.addCell(new CCell(rs.getString("tab_11")));

                     // Tab_12
                     colTab_12.addCell(new CCell(rs.getString("tab_12")));

                     // Tab_13
                     colTab_13.addCell(new CCell(rs.getString("tab_13")));

                     // Tab_14
                     colTab_14.addCell(new CCell(rs.getString("tab_14")));

                     // Tab_15
                     colTab_15.addCell(new CCell(rs.getString("tab_15")));

                     // Signer_balance
                     colSigner_balance.addCell(new CCell(rs.getString("signer_balance")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Hash
       c.addCell(colHash);

       // Block
       c.addCell(colBlock);

       // Prev_hash
       c.addCell(colPrev_hash);

       // Signer
       c.addCell(colSigner);

       // Packets
       c.addCell(colPackets);

       // Tstamp
       c.addCell(colTstamp);

       // Nonce
       c.addCell(colNonce);

       // Size
       c.addCell(colSize);

       // Net_dif
       c.addCell(colNet_dif);

       // Commited
       c.addCell(colCommited);

       // Confirmations
       c.addCell(colConfirmations);

       // Payload_hash
       c.addCell(colPayload_hash);

       // Tab_1
       c.addCell(colTab_1);

       // Tab_2
       c.addCell(colTab_2);

       // Tab_3
       c.addCell(colTab_3);

       // Tab_4
       c.addCell(colTab_4);

       // Tab_5
       c.addCell(colTab_5);

       // Tab_6
       c.addCell(colTab_6);

       // Tab_7
       c.addCell(colTab_7);

       // Tab_8
       c.addCell(colTab_8);

       // Tab_9
       c.addCell(colTab_9);

       // Tab_10
       c.addCell(colTab_10);

       // Tab_11
       c.addCell(colTab_11);

       // Tab_12
       c.addCell(colTab_12);

       // Tab_13
       c.addCell(colTab_13);

       // Tab_14
       c.addCell(colTab_14);

       // Tab_15
       c.addCell(colTab_15);

       // Signer_balance
       c.addCell(colSigner_balance);

       // Copy
       return c;
    }

    public CCell loadDomains() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Domain
       CCell colDomain=new CCell("");
       colDomain.name="domain";

       // Expire
       CCell colExpire=new CCell("");
       colExpire.name="expire";

       // Sale_price
       CCell colSale_price=new CCell("");
       colSale_price.name="sale_price";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Domain
                     colDomain.addCell(new CCell(rs.getString("domain")));

                     // Expire
                     colExpire.addCell(new CCell(rs.getString("expire")));

                     // Sale_price
                     colSale_price.addCell(new CCell(rs.getString("sale_price")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Adr
       c.addCell(colAdr);

       // Domain
       c.addCell(colDomain);

       // Expire
       c.addCell(colExpire);

       // Sale_price
       c.addCell(colSale_price);

       // Block
       c.addCell(colBlock);

       // Rowhash
       c.addCell(colRowhash);

       // Copy
       return c;
    }

    public CCell loadEscrowed() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Trans_hash
       CCell colTrans_hash=new CCell("");
       colTrans_hash.name="trans_hash";

       // Sender_adr
       CCell colSender_adr=new CCell("");
       colSender_adr.name="sender_adr";

       // Rec_adr
       CCell colRec_adr=new CCell("");
       colRec_adr.name="rec_adr";

       // Escrower
       CCell colEscrower=new CCell("");
       colEscrower.name="escrower";

       // Amount
       CCell colAmount=new CCell("");
       colAmount.name="amount";

       // Cur
       CCell colCur=new CCell("");
       colCur.name="cur";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Trans_hash
                     colTrans_hash.addCell(new CCell(rs.getString("trans_hash")));

                     // Sender_adr
                     colSender_adr.addCell(new CCell(rs.getString("sender_adr")));

                     // Rec_adr
                     colRec_adr.addCell(new CCell(rs.getString("rec_adr")));

                     // Escrower
                     colEscrower.addCell(new CCell(rs.getString("escrower")));

                     // Amount
                     colAmount.addCell(new CCell(rs.getString("amount")));

                     // Cur
                     colCur.addCell(new CCell(rs.getString("cur")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Trans_hash
       c.addCell(colTrans_hash);

       // Sender_adr
       c.addCell(colSender_adr);

       // Rec_adr
       c.addCell(colRec_adr);

       // Escrower
       c.addCell(colEscrower);

       // Amount
       c.addCell(colAmount);

       // Cur
       c.addCell(colCur);

       // Block
       c.addCell(colBlock);

       // Rowhash
       c.addCell(colRowhash);

       // Copy
       return c;
    }

    public CCell loadFeedsBets() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // MktID
       CCell colMktID=new CCell("");
       colMktID.name="mktID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Feed_1
       CCell colFeed_1=new CCell("");
       colFeed_1.name="feed_1";

       // Branch_1
       CCell colBranch_1=new CCell("");
       colBranch_1.name="branch_1";

       // Feed_2
       CCell colFeed_2=new CCell("");
       colFeed_2.name="feed_2";

       // Branch_2
       CCell colBranch_2=new CCell("");
       colBranch_2.name="branch_2";

       // Feed_3
       CCell colFeed_3=new CCell("");
       colFeed_3.name="feed_3";

       // Branch_3
       CCell colBranch_3=new CCell("");
       colBranch_3.name="branch_3";

       // Last_price
       CCell colLast_price=new CCell("");
       colLast_price.name="last_price";

       // Tip
       CCell colTip=new CCell("");
       colTip.name="tip";

       // Val_1
       CCell colVal_1=new CCell("");
       colVal_1.name="val_1";

       // Val_2
       CCell colVal_2=new CCell("");
       colVal_2.name="val_2";

       // Title
       CCell colTitle=new CCell("");
       colTitle.name="title";

       // Description
       CCell colDescription=new CCell("");
       colDescription.name="description";

       // Budget
       CCell colBudget=new CCell("");
       colBudget.name="budget";

       // Win_multiplier
       CCell colWin_multiplier=new CCell("");
       colWin_multiplier.name="win_multiplier";

       // Start_block
       CCell colStart_block=new CCell("");
       colStart_block.name="start_block";

       // End_block
       CCell colEnd_block=new CCell("");
       colEnd_block.name="end_block";

       // Accept_block
       CCell colAccept_block=new CCell("");
       colAccept_block.name="accept_block";

       // Cur
       CCell colCur=new CCell("");
       colCur.name="cur";

       // Bets
       CCell colBets=new CCell("");
       colBets.name="bets";

       // Invested
       CCell colInvested=new CCell("");
       colInvested.name="invested";

       // Status
       CCell colStatus=new CCell("");
       colStatus.name="status";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // MktID
                     colMktID.addCell(new CCell(rs.getString("mktID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Feed_1
                     colFeed_1.addCell(new CCell(rs.getString("feed_1")));

                     // Branch_1
                     colBranch_1.addCell(new CCell(rs.getString("branch_1")));

                     // Feed_2
                     colFeed_2.addCell(new CCell(rs.getString("feed_2")));

                     // Branch_2
                     colBranch_2.addCell(new CCell(rs.getString("branch_2")));

                     // Feed_3
                     colFeed_3.addCell(new CCell(rs.getString("feed_3")));

                     // Branch_3
                     colBranch_3.addCell(new CCell(rs.getString("branch_3")));

                     // Last_price
                     colLast_price.addCell(new CCell(rs.getString("last_price")));

                     // Tip
                     colTip.addCell(new CCell(rs.getString("tip")));

                     // Val_1
                     colVal_1.addCell(new CCell(rs.getString("val_1")));

                     // Val_2
                     colVal_2.addCell(new CCell(rs.getString("val_2")));

                     // Title
                     colTitle.addCell(new CCell(rs.getString("title")));

                     // Description
                     colDescription.addCell(new CCell(rs.getString("description")));

                     // Budget
                     colBudget.addCell(new CCell(rs.getString("budget")));

                     // Win_multiplier
                     colWin_multiplier.addCell(new CCell(rs.getString("win_multiplier")));

                     // Start_block
                     colStart_block.addCell(new CCell(rs.getString("start_block")));

                     // End_block
                     colEnd_block.addCell(new CCell(rs.getString("end_block")));

                     // Accept_block
                     colAccept_block.addCell(new CCell(rs.getString("accept_block")));

                     // Cur
                     colCur.addCell(new CCell(rs.getString("cur")));

                     // Bets
                     colBets.addCell(new CCell(rs.getString("bets")));

                     // Invested
                     colInvested.addCell(new CCell(rs.getString("invested")));

                     // Status
                     colStatus.addCell(new CCell(rs.getString("status")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // MktID
       c.addCell(colMktID);

       // Adr
       c.addCell(colAdr);

       // Feed_1
       c.addCell(colFeed_1);

       // Branch_1
       c.addCell(colBranch_1);

       // Feed_2
       c.addCell(colFeed_2);

       // Branch_2
       c.addCell(colBranch_2);

       // Feed_3
       c.addCell(colFeed_3);

       // Branch_3
       c.addCell(colBranch_3);

       // Last_price
       c.addCell(colLast_price);

       // Tip
       c.addCell(colTip);

       // Val_1
       c.addCell(colVal_1);

       // Val_2
       c.addCell(colVal_2);

       // Title
       c.addCell(colTitle);

       // Description
       c.addCell(colDescription);

       // Budget
       c.addCell(colBudget);

       // Win_multiplier
       c.addCell(colWin_multiplier);

       // Start_block
       c.addCell(colStart_block);

       // End_block
       c.addCell(colEnd_block);

       // Accept_block
       c.addCell(colAccept_block);

       // Cur
       c.addCell(colCur);

       // Bets
       c.addCell(colBets);

       // Invested
       c.addCell(colInvested);

       // Status
       c.addCell(colStatus);

       // Rowhash
       c.addCell(colRowhash);

       // Block
       c.addCell(colBlock);

       // Copy
       return c;
    }

    public CCell loadFeedsBetsPos() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // betID
       CCell colbetID=new CCell("");
       colbetID.name="betID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Amount
       CCell colAmount=new CCell("");
       colAmount.name="amount";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // betID
                     colbetID.addCell(new CCell(rs.getString("betID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Amount
                     colAmount.addCell(new CCell(rs.getString("amount")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // betID
       c.addCell(colbetID);

       // Adr
       c.addCell(colAdr);

       // Amount
       c.addCell(colAmount);

       // Block
       c.addCell(colBlock);

       // Rowhash
       c.addCell(colRowhash);

       // Copy
       return c;
    }

    public CCell loadFeedsSpecMkts() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Feed_1
       CCell colFeed_1=new CCell("");
       colFeed_1.name="feed_1";

       // Branch_1
       CCell colBranch_1=new CCell("");
       colBranch_1.name="branch_1";

       // Feed_2
       CCell colFeed_2=new CCell("");
       colFeed_2.name="feed_2";

       // Branch_2
       CCell colBranch_2=new CCell("");
       colBranch_2.name="branch_2";

       // Feed_3
       CCell colFeed_3=new CCell("");
       colFeed_3.name="feed_3";

       // Branch_3
       CCell colBranch_3=new CCell("");
       colBranch_3.name="branch_3";

       // Last_price
       CCell colLast_price=new CCell("");
       colLast_price.name="last_price";

       // Cur
       CCell colCur=new CCell("");
       colCur.name="cur";

       // Max_leverage
       CCell colMax_leverage=new CCell("");
       colMax_leverage.name="max_leverage";

       // Spread
       CCell colSpread=new CCell("");
       colSpread.name="spread";

       // Real_symbol
       CCell colReal_symbol=new CCell("");
       colReal_symbol.name="real_symbol";

       // Pos_type
       CCell colPos_type=new CCell("");
       colPos_type.name="pos_type";

       // Title
       CCell colTitle=new CCell("");
       colTitle.name="title";

       // Description
       CCell colDescription=new CCell("");
       colDescription.name="description";

       // Max_margin
       CCell colMax_margin=new CCell("");
       colMax_margin.name="max_margin";

       // Expire
       CCell colExpire=new CCell("");
       colExpire.name="expire";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Status
       CCell colStatus=new CCell("");
       colStatus.name="status";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // MktID
       CCell colMktID=new CCell("");
       colMktID.name="mktID";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Feed_1
                     colFeed_1.addCell(new CCell(rs.getString("feed_1")));

                     // Branch_1
                     colBranch_1.addCell(new CCell(rs.getString("branch_1")));

                     // Feed_2
                     colFeed_2.addCell(new CCell(rs.getString("feed_2")));

                     // Branch_2
                     colBranch_2.addCell(new CCell(rs.getString("branch_2")));

                     // Feed_3
                     colFeed_3.addCell(new CCell(rs.getString("feed_3")));

                     // Branch_3
                     colBranch_3.addCell(new CCell(rs.getString("branch_3")));

                     // Last_price
                     colLast_price.addCell(new CCell(rs.getString("last_price")));

                     // Cur
                     colCur.addCell(new CCell(rs.getString("cur")));

                     // Max_leverage
                     colMax_leverage.addCell(new CCell(rs.getString("max_leverage")));

                     // Spread
                     colSpread.addCell(new CCell(rs.getString("spread")));

                     // Real_symbol
                     colReal_symbol.addCell(new CCell(rs.getString("real_symbol")));

                     // Pos_type
                     colPos_type.addCell(new CCell(rs.getString("pos_type")));

                     // Title
                     colTitle.addCell(new CCell(rs.getString("title")));

                     // Description
                     colDescription.addCell(new CCell(rs.getString("description")));

                     // Max_margin
                     colMax_margin.addCell(new CCell(rs.getString("max_margin")));

                     // Expire
                     colExpire.addCell(new CCell(rs.getString("expire")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Status
                     colStatus.addCell(new CCell(rs.getString("status")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

                     // MktID
                     colMktID.addCell(new CCell(rs.getString("mktID")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Adr
       c.addCell(colAdr);

       // Feed_1
       c.addCell(colFeed_1);

       // Branch_1
       c.addCell(colBranch_1);

       // Feed_2
       c.addCell(colFeed_2);

       // Branch_2
       c.addCell(colBranch_2);

       // Feed_3
       c.addCell(colFeed_3);

       // Branch_3
       c.addCell(colBranch_3);

       // Last_price
       c.addCell(colLast_price);

       // Cur
       c.addCell(colCur);

       // Max_leverage
       c.addCell(colMax_leverage);

       // Spread
       c.addCell(colSpread);

       // Real_symbol
       c.addCell(colReal_symbol);

       // Pos_type
       c.addCell(colPos_type);

       // Title
       c.addCell(colTitle);

       // Description
       c.addCell(colDescription);

       // Max_margin
       c.addCell(colMax_margin);

       // Expire
       c.addCell(colExpire);

       // Block
       c.addCell(colBlock);

       // Status
       c.addCell(colStatus);

       // Rowhash
       c.addCell(colRowhash);

       // MktID
       c.addCell(colMktID);

       // Copy
       return c;
    }

    public CCell loadFeedsSpecMktsPos() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // MktID
       CCell colMktID=new CCell("");
       colMktID.name="mktID";

       // PosID
       CCell colPosID=new CCell("");
       colPosID.name="posID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Open
       CCell colOpen=new CCell("");
       colOpen.name="open";

       // Sl
       CCell colSl=new CCell("");
       colSl.name="sl";

       // Tp
       CCell colTp=new CCell("");
       colTp.name="tp";

       // Leverage
       CCell colLeverage=new CCell("");
       colLeverage.name="leverage";

       // Qty
       CCell colQty=new CCell("");
       colQty.name="qty";

       // Status
       CCell colStatus=new CCell("");
       colStatus.name="status";

       // Open_line
       CCell colOpen_line=new CCell("");
       colOpen_line.name="open_line";

       // Tip
       CCell colTip=new CCell("");
       colTip.name="tip";

       // Pl
       CCell colPl=new CCell("");
       colPl.name="pl";

       // Spread
       CCell colSpread=new CCell("");
       colSpread.name="spread";

       // Margin
       CCell colMargin=new CCell("");
       colMargin.name="margin";

       // Close_reason
       CCell colClose_reason=new CCell("");
       colClose_reason.name="close_reason";

       // Closed_pl
       CCell colClosed_pl=new CCell("");
       colClosed_pl.name="closed_pl";

       // Closed_margin
       CCell colClosed_margin=new CCell("");
       colClosed_margin.name="closed_margin";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Last_block
       CCell colLast_block=new CCell("");
       colLast_block.name="last_block";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // MktID
                     colMktID.addCell(new CCell(rs.getString("mktID")));

                     // PosID
                     colPosID.addCell(new CCell(rs.getString("posID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Open
                     colOpen.addCell(new CCell(rs.getString("open")));

                     // Sl
                     colSl.addCell(new CCell(rs.getString("sl")));

                     // Tp
                     colTp.addCell(new CCell(rs.getString("tp")));

                     // Leverage
                     colLeverage.addCell(new CCell(rs.getString("leverage")));

                     // Qty
                     colQty.addCell(new CCell(rs.getString("qty")));

                     // Status
                     colStatus.addCell(new CCell(rs.getString("status")));

                     // Open_line
                     colOpen_line.addCell(new CCell(rs.getString("open_line")));

                     // Tip
                     colTip.addCell(new CCell(rs.getString("tip")));

                     // Pl
                     colPl.addCell(new CCell(rs.getString("pl")));

                     // Spread
                     colSpread.addCell(new CCell(rs.getString("spread")));

                     // Margin
                     colMargin.addCell(new CCell(rs.getString("margin")));

                     // Close_reason
                     colClose_reason.addCell(new CCell(rs.getString("close_reason")));

                     // Closed_pl
                     colClosed_pl.addCell(new CCell(rs.getString("closed_pl")));

                     // Closed_margin
                     colClosed_margin.addCell(new CCell(rs.getString("closed_margin")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Last_block
                     colLast_block.addCell(new CCell(rs.getString("last_block")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // MktID
       c.addCell(colMktID);

       // PosID
       c.addCell(colPosID);

       // Adr
       c.addCell(colAdr);

       // Open
       c.addCell(colOpen);

       // Sl
       c.addCell(colSl);

       // Tp
       c.addCell(colTp);

       // Leverage
       c.addCell(colLeverage);

       // Qty
       c.addCell(colQty);

       // Status
       c.addCell(colStatus);

       // Open_line
       c.addCell(colOpen_line);

       // Tip
       c.addCell(colTip);

       // Pl
       c.addCell(colPl);

       // Spread
       c.addCell(colSpread);

       // Margin
       c.addCell(colMargin);

       // Close_reason
       c.addCell(colClose_reason);

       // Closed_pl
       c.addCell(colClosed_pl);

       // Closed_margin
       c.addCell(colClosed_margin);

       // Block
       c.addCell(colBlock);

       // Last_block
       c.addCell(colLast_block);

       // Rowhash
       c.addCell(colRowhash);

       // Copy
       return c;
    }

    public CCell loadpackets() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Packet_hash
       CCell colPacket_hash=new CCell("");
       colPacket_hash.name="packet_hash";

       // Par_1_name
       CCell colPar_1_name=new CCell("");
       colPar_1_name.name="par_1_name";

       // Par_1_val
       CCell colPar_1_val=new CCell("");
       colPar_1_val.name="par_1_val";

       // Par_2_name
       CCell colPar_2_name=new CCell("");
       colPar_2_name.name="par_2_name";

       // Par_2_val
       CCell colPar_2_val=new CCell("");
       colPar_2_val.name="par_2_val";

       // Par_3_name
       CCell colPar_3_name=new CCell("");
       colPar_3_name.name="par_3_name";

       // Par_3_val
       CCell colPar_3_val=new CCell("");
       colPar_3_val.name="par_3_val";

       // Par_4_name
       CCell colPar_4_name=new CCell("");
       colPar_4_name.name="par_4_name";

       // Par_4_val
       CCell colPar_4_val=new CCell("");
       colPar_4_val.name="par_4_val";

       // Par_5_name
       CCell colPar_5_name=new CCell("");
       colPar_5_name.name="par_5_name";

       // Par_5_val
       CCell colPar_5_val=new CCell("");
       colPar_5_val.name="par_5_val";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Tstamp
       CCell colTstamp=new CCell("");
       colTstamp.name="tstamp";

       // Confirms
       CCell colConfirms=new CCell("");
       colConfirms.name="confirms";

       // Block_hash
       CCell colBlock_hash=new CCell("");
       colBlock_hash.name="block_hash";

       // Payload_hash
       CCell colPayload_hash=new CCell("");
       colPayload_hash.name="payload_hash";

       // Payload_size
       CCell colPayload_size=new CCell("");
       colPayload_size.name="payload_size";

       // Packet_type
       CCell colPacket_type=new CCell("");
       colPacket_type.name="packet_type";

       // Fee_src
       CCell colFee_src=new CCell("");
       colFee_src.name="fee_src";

       // Fee_amount
       CCell colFee_amount=new CCell("");
       colFee_amount.name="fee_amount";

       // Fee_hash
       CCell colFee_hash=new CCell("");
       colFee_hash.name="fee_hash";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Packet_hash
                     colPacket_hash.addCell(new CCell(rs.getString("packet_hash")));

                     // Par_1_name
                     colPar_1_name.addCell(new CCell(rs.getString("par_1_name")));

                     // Par_1_val
                     colPar_1_val.addCell(new CCell(rs.getString("par_1_val")));

                     // Par_2_name
                     colPar_2_name.addCell(new CCell(rs.getString("par_2_name")));

                     // Par_2_val
                     colPar_2_val.addCell(new CCell(rs.getString("par_2_val")));

                     // Par_3_name
                     colPar_3_name.addCell(new CCell(rs.getString("par_3_name")));

                     // Par_3_val
                     colPar_3_val.addCell(new CCell(rs.getString("par_3_val")));

                     // Par_4_name
                     colPar_4_name.addCell(new CCell(rs.getString("par_4_name")));

                     // Par_4_val
                     colPar_4_val.addCell(new CCell(rs.getString("par_4_val")));

                     // Par_5_name
                     colPar_5_name.addCell(new CCell(rs.getString("par_5_name")));

                     // Par_5_val
                     colPar_5_val.addCell(new CCell(rs.getString("par_5_val")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Tstamp
                     colTstamp.addCell(new CCell(rs.getString("tstamp")));

                     // Confirms
                     colConfirms.addCell(new CCell(rs.getString("confirms")));

                     // Block_hash
                     colBlock_hash.addCell(new CCell(rs.getString("block_hash")));

                     // Payload_hash
                     colPayload_hash.addCell(new CCell(rs.getString("payload_hash")));

                     // Payload_size
                     colPayload_size.addCell(new CCell(rs.getString("payload_size")));

                     // Packet_type
                     colPacket_type.addCell(new CCell(rs.getString("packet_type")));

                     // Fee_src
                     colFee_src.addCell(new CCell(rs.getString("fee_src")));

                     // Fee_amount
                     colFee_amount.addCell(new CCell(rs.getString("fee_amount")));

                     // Fee_hash
                     colFee_hash.addCell(new CCell(rs.getString("fee_hash")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Packet_hash
       c.addCell(colPacket_hash);

       // Par_1_name
       c.addCell(colPar_1_name);

       // Par_1_val
       c.addCell(colPar_1_val);

       // Par_2_name
       c.addCell(colPar_2_name);

       // Par_2_val
       c.addCell(colPar_2_val);

       // Par_3_name
       c.addCell(colPar_3_name);

       // Par_3_val
       c.addCell(colPar_3_val);

       // Par_4_name
       c.addCell(colPar_4_name);

       // Par_4_val
       c.addCell(colPar_4_val);

       // Par_5_name
       c.addCell(colPar_5_name);

       // Par_5_val
       c.addCell(colPar_5_val);

       // Block
       c.addCell(colBlock);

       // Tstamp
       c.addCell(colTstamp);

       // Confirms
       c.addCell(colConfirms);

       // Block_hash
       c.addCell(colBlock_hash);

       // Payload_hash
       c.addCell(colPayload_hash);

       // Payload_size
       c.addCell(colPayload_size);

       // Packet_type
       c.addCell(colPacket_type);

       // Fee_src
       c.addCell(colFee_src);

       // Fee_amount
       c.addCell(colFee_amount);

       // Fee_hash
       c.addCell(colFee_hash);

       // Copy
       return c;
    }

    public CCell loadTweets() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // TweetID
       CCell colTweetID=new CCell("");
       colTweetID.name="tweetID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Mes
       CCell colMes=new CCell("");
       colMes.name="mes";

       // Pic_1
       CCell colPic_1=new CCell("");
       colPic_1.name="pic_1";

       // Pic_2
       CCell colPic_2=new CCell("");
       colPic_2.name="pic_2";

       // Pic_3
       CCell colPic_3=new CCell("");
       colPic_3.name="pic_3";

       // Pic_4
       CCell colPic_4=new CCell("");
       colPic_4.name="pic_4";

       // Pic_5
       CCell colPic_5=new CCell("");
       colPic_5.name="pic_5";

       // Video
       CCell colVideo=new CCell("");
       colVideo.name="video";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Retweet
       CCell colRetweet=new CCell("");
       colRetweet.name="retweet";

       // Retweet_tweet_ID
       CCell colRetweet_tweet_ID=new CCell("");
       colRetweet_tweet_ID.name="retweet_tweet_ID";

       // Likes
       CCell colLikes=new CCell("");
       colLikes.name="likes";

       // Comments
       CCell colComments=new CCell("");
       colComments.name="comments";

       // Retweets
       CCell colRetweets=new CCell("");
       colRetweets.name="retweets";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // TweetID
                     colTweetID.addCell(new CCell(rs.getString("tweetID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Mes
                     colMes.addCell(new CCell(rs.getString("mes")));

                     // Pic_1
                     colPic_1.addCell(new CCell(rs.getString("pic_1")));

                     // Pic_2
                     colPic_2.addCell(new CCell(rs.getString("pic_2")));

                     // Pic_3
                     colPic_3.addCell(new CCell(rs.getString("pic_3")));

                     // Pic_4
                     colPic_4.addCell(new CCell(rs.getString("pic_4")));

                     // Pic_5
                     colPic_5.addCell(new CCell(rs.getString("pic_5")));

                     // Video
                     colVideo.addCell(new CCell(rs.getString("video")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Retweet
                     colRetweet.addCell(new CCell(rs.getString("retweet")));

                     // Retweet_tweet_ID
                     colRetweet_tweet_ID.addCell(new CCell(rs.getString("retweet_tweet_ID")));

                     // Likes
                     colLikes.addCell(new CCell(rs.getString("likes")));

                     // Comments
                     colComments.addCell(new CCell(rs.getString("comments")));

                     // Retweets
                     colRetweets.addCell(new CCell(rs.getString("retweets")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // TweetID
       c.addCell(colTweetID);

       // Adr
       c.addCell(colAdr);

       // Mes
       c.addCell(colMes);

       // Pic_1
       c.addCell(colPic_1);

       // Pic_2
       c.addCell(colPic_2);

       // Pic_3
       c.addCell(colPic_3);

       // Pic_4
       c.addCell(colPic_4);

       // Pic_5
       c.addCell(colPic_5);

       // Video
       c.addCell(colVideo);

       // Rowhash
       c.addCell(colRowhash);

       // Block
       c.addCell(colBlock);

       // Retweet
       c.addCell(colRetweet);

       // Retweet_tweet_ID
       c.addCell(colRetweet_tweet_ID);

       // Likes
       c.addCell(colLikes);

       // Comments
       c.addCell(colComments);

       // Retweets
       c.addCell(colRetweets);

       // Copy
       return c;
    }

    public CCell loadVotes() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";
       
       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";
       
       // Target type
       CCell colTarget_type=new CCell("");
       colTarget_type.name="target_type";

       // Target ID
       CCell colTargetID=new CCell("");
       colTargetID.name="targetID";
       
       // Power
       CCell colPower=new CCell("");
       colPower.name="power";
      
       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));
                     
                     // Target type
                     colTarget_type.addCell(new CCell(rs.getString("target_type")));

                     // Target ID
                     colTargetID.addCell(new CCell(rs.getString("targetID")));
                     
                     // Power
                     colPower.addCell(new CCell(rs.getString("power")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);
 
       // Adr
       c.addCell(colAdr);
       
       // Target type
       c.addCell(colTarget_type);

       // Target ID
       c.addCell(colTargetID);
       
       // Power
       c.addCell(colPower);

       // Block
       c.addCell(colBlock);

       // Rowhash
       c.addCell(colRowhash);

       // Copy
       return c;
    }

    public CCell loadTweetsFollow() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Follows
       CCell colFollows=new CCell("");
       colFollows.name="follows";

       // Expire
       CCell colExpire=new CCell("");
       colExpire.name="expire";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Follows
                     colFollows.addCell(new CCell(rs.getString("follows")));

                     // Expire
                     colExpire.addCell(new CCell(rs.getString("expire")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Adr
       c.addCell(colAdr);

       // Follows
       c.addCell(colFollows);

       // Expire
       c.addCell(colExpire);

       // Block
       c.addCell(colBlock);

       // Rowhash
       c.addCell(colRowhash);

       // Copy
       return c;
    }

    public CCell loadComments() throws Exception
    {
       // Result
       ResultSet rs=this.query();

       // Creates new cell
       CCell c=new CCell("");

       // ID
       CCell colID=new CCell("");
       colID.name="ID";

       // Adr
       CCell colAdr=new CCell("");
       colAdr.name="adr";

       // Parent_type
       CCell colParent_type=new CCell("");
       colParent_type.name="parent_type";

       // ParentID
       CCell colParentID=new CCell("");
       colParentID.name="parentID";

       // ComID
       CCell colComID=new CCell("");
       colComID.name="comID";

       // Mes
       CCell colMes=new CCell("");
       colMes.name="mes";

       // Rowhash
       CCell colRowhash=new CCell("");
       colRowhash.name="rowhash";

       // Block
       CCell colBlock=new CCell("");
       colBlock.name="block";

       // Has data
       if (UTILS.DB.hasData(rs))
          {
             // Load data
             while (rs.next())
             {
                     // ID
                     colID.addCell(new CCell(rs.getString("ID")));

                     // Adr
                     colAdr.addCell(new CCell(rs.getString("adr")));

                     // Parent_type
                     colParent_type.addCell(new CCell(rs.getString("parent_type")));

                     // ParentID
                     colParentID.addCell(new CCell(rs.getString("parentID")));

                     // ComID
                     colComID.addCell(new CCell(rs.getString("comID")));

                     // Mes
                     colMes.addCell(new CCell(rs.getString("mes")));

                     // Rowhash
                     colRowhash.addCell(new CCell(rs.getString("rowhash")));

                     // Block
                     colBlock.addCell(new CCell(rs.getString("block")));

              }
       }
       else
       {
              c.copy(new CCell(0));
              return c;
       }

       // ID
       c.addCell(colID);

       // Adr
       c.addCell(colAdr);

       // Parent_type
       c.addCell(colParent_type);

       // ParentID
       c.addCell(colParentID);

       // ComID
       c.addCell(colComID);

       // Mes
       c.addCell(colMes);

       // Rowhash
       c.addCell(colRowhash);

       // Block
       c.addCell(colBlock);

       // Copy
       return c;
    }

    
    public void execute() throws Exception
    {
        // Log 
        VM.RUNLOG.add(VM.REGS.RCI, "API "+this.dest.cel.name+", "
                                         +this.table.cel.val+", "
                                         +this.col.cel.val+", "
                                         +this.type.cel.val+", "
                                         +this.val.cel.val+", "
                                         +this.min.cel.val+", "
                                         +this.max.cel.val);
        
        // Check table
        if (!this.tableValid(this.table.cel.val))
            throw new Exception("Invalid table");
            
        // Check column
        if (!this.colValid(table.cel.val, col.cel.val))
            throw new Exception("Invalid column");
        
        // Return
        CCell c=new CCell(0);
        
        // Load data
        switch (this.table.cel.val)
        {
            // Adr
            case "adr" : c=this.loadAdr(); break;
            
            // Ads
            case "ads" : c=this.loadAds(); break;
            
            // Agents
            case "agents" : c=this.loadAgents(); break;
            
            // Assets
            case "assets" : c=this.loadAssets(); break;
            
            // Assets owners
            case "assets_owners" : c=this.loadAssetsOwners(); break;
            
            // Assets markets
            case "assets_mkts" : c=this.loadAssetsMkts(); break;
            
            // Assets markets pos
            case "assets_mkts_pos" : c=this.loadAssetsMktsPos(); break;
            
            // Domains
            case "domains" : c=this.loadDomains(); break;
            
            // Escrowed
            case "escrowed" : c=this.loadEscrowed(); break;
            
            // Feeds speculative markets
            case "feeds_spec_mkts" : c=this.loadFeedsSpecMkts(); break;
            
            // Feeds speculative markets pos
            case "feeds_spec_mkts_pos" : c=this.loadFeedsSpecMktsPos(); break;
            
            // Feeds bets
            case "feeds_bets" : c=this.loadFeedsBets(); break;
            
            // Feeds bets pos
            case "feeds_bets_pos" : c=this.loadFeedsBetsPos(); break;
            
            // Tweets
            case "tweets" : c=this.loadTweets(); break;
            
            // Tweets likes
            case "votes" : c=this.loadVotes(); break;
            
            // Tweets follow
            case "tweets_follow" : c=this.loadTweetsFollow(); break;
            
            // Tweets comments
            case "comments" : c=this.loadComments(); break;
        }
        
        // Copy to dest
        this.dest.cel.copy(c);
    }
}
