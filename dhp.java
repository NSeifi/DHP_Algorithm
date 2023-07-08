/*
* Nasrin Seifi 230137517
* DHP
* */


import java.util.*;
import java.io.*;
import java.lang.*;

public class dhp
{
    public static Integer minSupport;
    public static String address;
    public static Hashtable<Integer, ArrayList<Set<String>>> h2=new Hashtable<Integer, ArrayList<Set<String>>>();
    public static Double minis;

    public static void main(String [] args) throws IOException {
        Scanner sc =new Scanner(System.in);
        address=sc.nextLine();
        minis=sc.nextDouble();
        /*address=args[0];
        minis=Double.parseDouble(args[1]);*/
        HashMap<String, Integer> itemList1=new  HashMap<String, Integer>();
        itemList1= readFile();
        itemList1=minSup(itemList1);
        System.out.println(itemList1+"items");
        System.out.println(h2+"hash");

        HashMap<String, Integer> ck = new HashMap<String, Integer>();
        HashMap<String, Integer> lk = new HashMap<String, Integer>();
        int k=1;
        while(true)
        {
            k++;
            if (k==2)
            {
                ck = geneItemSet(itemList1,k);
                ck=prunehash(ck);
                ck = countItemSet(ck,k);

                if(!ck.isEmpty())
                    lk=ck;

                else
                {
                    System.out.println(" The largest frequent itemset is: "+itemList1);
                    break;
                };
            }//if
            else
            {
                ck = geneItemSet(lk,k);
                if(!ck.isEmpty()) {
                    ck = countItemSet(ck,k);
                    lk = ck;
                }
                else
                {
                    System.out.println(" The largest frequent itemset is: "+lk);
                    break;
                };
            }//else
        }

    }//main

    private static  HashMap<String, Integer> readFile()  throws FileNotFoundException {
        HashMap<String, Integer> items = new HashMap<String, Integer> ();
        File file = new File(address);
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            String[] arr = str.split("\\s+");
            if (items.isEmpty()) {
                items.put(arr[0],0);
                minSupport= Math.toIntExact(Math.round(Integer.parseInt(arr[0]) * minis / 100));
                System.out.println(minSupport+"min sup "+minis);
            }//if
            else{
                for (int j = 2; j < arr.length; j++)
                {
                    if (items.containsKey(arr[j]))
                        items.replace(arr[j],items.get(arr[j]),items.get(arr[j])+1);
                    else
                        items.put(arr[j],1);
                }

            hashFnc(arr);
            }//else
        }//while

        for (int i=0 ; i< 7; i++)
        {
            if (h2.containsKey(i) && h2.get(i).size()<minSupport)
                h2.remove(i);
        }

        return items;
    }//readFile

    private static void hashFnc(String[] transaction) {
        ArrayList<Set<String>> list;
        Set<String> sethash;
        for(int i=2; i<transaction.length;i++) {
            for (int j=i+1; j<transaction.length;j++) {
                sethash = new HashSet<String>();
                list = new ArrayList<Set<String>>();;
                int k = (Integer.parseInt(transaction[i]) * 10 + Integer.parseInt(transaction[j])) % 7;
                sethash.add(transaction[i]);
                sethash.add(transaction[j]);
                if (h2.containsKey(k))
                {
                    list=h2.get(k);
                    list.add(sethash);
                    h2.put(k,list);
                }
                else
                {
                    list.add(sethash);
                    h2.put(k,list);
                }
            }
        }
    }

    private static HashMap<String, Integer> minSup(HashMap<String, Integer> itemSet)
    {
        HashMap<String, Integer> itemR = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> pair: itemSet.entrySet()) {
            if (pair.getValue()>=minSupport)
                itemR.put(pair.getKey(),pair.getValue());

        }
        return itemR;
    }//minSup

    private static HashMap<String, Integer> geneItemSet(HashMap<String, Integer> itemSet, int k)
    {
        ArrayList<String> itemList1 = new ArrayList<>(itemSet.keySet());
        HashMap<Set<String>,Integer> cList=new HashMap<Set<String>, Integer>();
        HashMap<String,Integer> cListR=new HashMap<String, Integer>();

        for(int i=0; i<itemList1.size();i++) {
            for (int j=i+1; j<itemList1.size();j++)
            {

                Set<String> st1= new HashSet<String>(Arrays.asList(itemList1.get(i).split(",")));
                Set<String> st2= new HashSet<String>(Arrays.asList(itemList1.get(j).split(",")));
                st1.addAll(st2);

                if (st1.size()==k)
                    //if (!cList.containsKey(st1))
                        cList.put(st1,0);

            }//for
        }
        for (Map.Entry<Set<String>, Integer> pair: cList.entrySet()) {
            String joined = String.join(",", pair.getKey());
            cListR.put(joined,0);
        }
        return cListR;
    }//genItemSet

    private static HashMap<String, Integer> countItemSet(HashMap<String, Integer> ck , int k )
            throws IOException {
        HashMap<String, Integer> itemR = new HashMap<String, Integer>();
        String data;
        int flag=0;
        File file = new File(address);
        File temp = File.createTempFile("temp-file-name",".tmp");
        PrintWriter pw= new PrintWriter(new FileWriter(temp));
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        while ((data=br.readLine())!= null) {
            Set<String> trans = new HashSet<String>();
            Set<String> counttrans = new HashSet<String>();
            HashMap<String,Integer> countitem = new HashMap<String, Integer>();
            String[] arr = data.split("\\s+");
            for (int i = 2; i < arr.length; i++)
                trans.add(arr[i]);

            for (Map.Entry<String, Integer> pair : ck.entrySet()) {
                Set<String> ckset = new HashSet<String>(Arrays.asList(pair.getKey().split(",")));
                if (trans.containsAll(ckset)) {
                    ck.replace(pair.getKey(), pair.getValue(), pair.getValue() + 1);
                    counttrans.add(pair.getKey());
                }
            }//for
           //reduce tarnsaction and items DB
            if (counttrans.size() > k) {
                for (String st : counttrans) {
                    String[] itm = st.split(",");
                    for (String itms : itm) {
                        if (!countitem.containsKey(itms))
                            countitem.put(itms,1);
                        else
                        {
                            int j=countitem.get(itms);
                            countitem.put(itms,j+1);
                        }
                    } //count item in k-itemset a transaction
            }//for
                for (Map.Entry<String, Integer> pair : countitem.entrySet()) {
                    if(pair.getValue()< k)
                       trans.remove(pair.getKey());
                }

                String joined = String.join(" ", trans);
                data=arr[0]+" " +arr[1]+" "+joined;
                pw.println(data);
                flag=1;
            }//if

        }//while
        ck=minSup(ck);
        br.close();
        if (flag==1) {
            pw.close();
            file.delete();
            temp.renameTo(file);
        }
        return ck;
    }//countItemSet

    private static HashMap<String, Integer> prunehash(HashMap<String, Integer> ck)
    {
        HashMap<String, Integer> itemR = new HashMap<String, Integer>();
        ArrayList<Set<String>> temp=new ArrayList<Set<String>>();
        Set<String> tempset ;
        for (int i=0;i<7;i++) {
            if (h2.containsKey(i)) {
                temp = h2.get(i);
                for (Map.Entry<String, Integer> pair : ck.entrySet()) {
                    tempset=new HashSet<String>(Arrays.asList(pair.getKey().split(",")));
                    if (temp.contains(tempset)) {
                        itemR.put(pair.getKey(), pair.getValue());
                    }
                }
            }
        }

        return itemR;
    }//prunehash

}//class
