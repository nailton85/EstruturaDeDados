
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;


public class ChainingHashST<Key, Value>{
	private static final int INIT_CAPACITY = 4;
	
	private int n;
	private int m;
	private DequeSearch<Key, Value>[] st;
	
	/**
	*Initializes an empty symbol table.
	*/
	public ChainingHashST(){
		this(INIT_CAPACITY);
	}
	
	/**
	*Initializes an empty simbol table with{@code m} chains.
	*@param m the initial number of chains
	*/
	public ChainingHashST(int m){
		this.m = m;
		st = (DequeSearch<Key, Value>[]) new DequeSearch[m];
		for (int i = 0; i < m; i++)
			st[i] = new DequeSearch<Key, Value>();
	}
	//resize the hash table to have the given number of chains,
	//rehashing all of the keys
	private void resize (int chains){
		ChainingHashST<Key, Value> temp = new ChainingHashST<Key, Value>(chains);
		for(int i = 0; i < m; i++){
			for(Key key : st[i].keys()){
				temp.put(key, st[i].get(key));
			}
		}
		this.m = temp.m;
		this.n = temp.n;
		this.st = temp.st;
	}
	
	//hash function for key - returns value between 0 and m-1
	private int hashTextbook(Key key){
		return(Math.abs(key.hashCode())) % m;
	}
	
	//hash function for keys - returns value between 0 and m-1 (assmes m is a power of 2)
	//(from java 7 implementation, protects against poor quality hashCode() implementations)
	private int hash(Key key){
		int h = Math.abs(key.hashCode());
		double ftmp = ((Math.sqrt(5) - 1) / 2) * h;
		int itmp = (int) ftmp;
		ftmp = ftmp - itmp;
		itmp = (int) (ftmp * m);
		return itmp;
//		int h = (Math.abs(key.hashCode()));
//		h ^= (h >>> 20) ^ (h >>> 12) ^ (h >>> 7) ^ (h >>> 4);
//		return h & (m - 1);
	}
	
	/**
	*Returns the number for key - value pairs in this symbol table.
	*
	*@return the number of key - value pairs in this symbol table
	*/
	public int size(){
		return n;
	}
	
	/**
	*Returns true if this simbol table is empty.
	*
	*@return {@code true} if this symbol table is empty
	*		 {@code false} othewise
	*/
	public boolean isEmpty(){
		if(size() == 0){
			return  true;
		}else{
			return false;
		}
//		return size() == 0;
	}
	
	/**
	*Returns the value associated with the specified key in this symbol table.
	*
	*@param key the key
	*@return the value associated with {@code key} in the simbol table;
	*		 {@code null} if no such value
	*@throws IllegalArgumentException if {@code key} is {@code null}
	*/
	public Value get(Key key){
		if (key == null) throw new IllegalArgumentException("argument to get()is null");
		int i = hash(key);
		return st[i].get(key);
	}
	
	/**
	*Iserts the specified key - value pair into the symbol table, overwriting the old
	*value with the new value if the symbol table already contains the specified key.
	*Deletes the specified key (and its associated value) from this symbol table
	*if the specified value is {@code null}.
	*
	*@param key the key
	*@param val the value
	*@throws IllegalArgumentException if {@code key} is {@code null}
	*/
	public void put(Key key, Value val){
		if(key == null) throw new IllegalArgumentException("first argument to put() is null");
		if(val == null){
			delete(key);
			return;
		}
		
		//double table size if average length of list >= 10
		if(n >= 10*m) resize(2*m);
		
		int i = hash(key);
		
		if(!st[i].contains(key)) n++;
		st[i].put(key, val);
	}
	/**
	*Removes the specified key and its associated value from this symbol table
	*(if the key is in this symbol table).
	*
	*@param key the key
	*@throws IllegalArgumentException if {@code key} is {@code null}
	*/
	public void delete(Key key){
		if(key == null) throw new IllegalArgumentException("argument to delete() is null");
		
		int i = hash(key);
		if(st[i].contains(key)) n--;
		st[i].delete(key);
		
		//halve table size if average length of list <= 2
		if(m > INIT_CAPACITY && n <= 2*m) resize(m/2);
	}
	
	//return key in symbol table as an Interable
	public Iterable<Key> keys(){
		Deque<Key> queue = new Deque<Key>();
		for(int i = 0; i < m; i++){
			for(Key key : st[i].keys())
				queue.push_back(key);
		}
		return queue;
	}
	
	 /**
	 *Unit tests the {@code SeparateChainingHashST} data type.
	 *
	 *@param args the command-line arguments
	 */
	 public static void main(String [] args){
		 if(args.length < 2){
			 System.out.println("\n\nUso: java ChainingHashST arquivo1 arquivo2\n\n");
			 System.exit(0);
		 }
		 int n;
		 
		 String tmp;
		 StringTokenizer st;
		 ChainingHashST<String, Cidade> tabelahash = new ChainingHashST<String, Cidade>(16);
		 
		 Cidade city;
		 
		 try{
			 FileReader in1 = new FileReader (args[0]);
			 BufferedReader br = new BufferedReader(in1);
			 n = Integer.parseInt(br.readLine());
			 
			 for(int j = 0; j < n; j++){
				 tmp = br.readLine();
				 st = new StringTokenizer(tmp);
				 
				 city = new Cidade(st.nextToken(), Integer.parseInt(st.nextToken()));
				 tabelahash.put(city.get_nome(),  city);
			 }
			 br.close();
			 in1.close();
			 
			 in1 = new FileReader (args[1]);
			 br = new BufferedReader(in1);
			 
			 n = Integer.parseInt(br.readLine());
			 
			 for(int j = 0; j < n; j++){
				 tmp = br.readLine();
				 
				 city = tabelahash.get(tmp);
				 if(city == null) System.out.print("\n[Failed] " +tmp+ " não foi encontrada.");
				 else{
					 System.out.print("\n[Ok]\t "+city.get_nome()+" foi encontrada. Temperatura lá é"+city.get_temp()+" F");
				 }
			 }
			 
			 br.close();
			 in1.close();
			 
			 System.out.print("\n");
			 
		 }catch(IOException e){
		 
		 }
	 }	 
}