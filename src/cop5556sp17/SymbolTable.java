package cop5556sp17;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Dec;

class tbEntry{
	int scope;
	Dec dec;
	tbEntry(int scope, Dec dec){
		this.scope = scope;
		this.dec = dec;
	}
}
public class SymbolTable {
	
	
	//TODO  add fields
	public Stack<Integer> scope;
	public Map<String, List<tbEntry>> table;
	private int current_scope;
	private int next_scope;
	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		current_scope = next_scope++;
		scope.push(current_scope);
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		//TODO:  IMPLEMENT THIS
		current_scope=scope.pop();
	}
	
	public boolean insert(String ident, Dec dec){
		//TODO:  IMPLEMENT THIS
		//System.out.println(ident);
		tbEntry temp = new tbEntry(current_scope, dec);
		if(table.containsKey(ident)){
			for(int i=table.get(ident).size()-1;i>=0;i--){
				if(table.get(ident).get(i).scope==current_scope)
					return false;
			}
			table.get(ident).add(temp);
		}
		else{
			List<tbEntry> list = new ArrayList<>();
			list.add(temp);
			table.put(ident, list);
		}
		//System.out.println(ident);
		return true;
	}
	
	public Dec lookup(String ident){
		//TODO:  IMPLEMENT THIS
		int ss=0;
		//System.out.println(ident+" "+table.get(ident).get(0).scope);
		if(table.containsKey(ident)){
			List<tbEntry> temp = table.get(ident);
			int min = Integer.MAX_VALUE;
			Dec temp1 = null;
			//int ss = 0;
			//System.out.println(scope.get(0));
			for(int i=temp.size()-1;i>=0;i--){
				if(scope.contains(temp.get(i).scope)){
					if(current_scope-temp.get(i).scope<min){
						ss = temp.get(i).scope;
						min = current_scope-temp.get(i).scope;
						temp1 = temp.get(i).dec;
					}
					//System.out.println(min);
				}
			}
			return temp1;
		}
		return null;
	}
		
	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
		scope = new Stack<Integer>();
		table = new HashMap<String, List<tbEntry>>();
		current_scope = 0;
		next_scope = 0;
		//scope.push(current_scope);
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return table.toString();
	}
	
	/*public static void main(String[] args){
		SymbolTable s = new SymbolTable();
		s.current_scope=5;
		s.scope.push(0);
		s.scope.push(1);
		s.scope.push(2);
		s.scope.push(3);
		s.scope.push(4);
		tbEntry t1 = new tbEntry(0, new Dec(null, null));
		tbEntry t2 = new tbEntry(1, new Dec(null, null));
		tbEntry t3 = new tbEntry(2, new Dec(null, null));
		tbEntry t4 = new tbEntry(3, new Dec(null, null));
		List<tbEntry> list = new ArrayList<>();
		list.add(t1);
		list.add(t2);
		list.add(t3);
		list.add(t4);
		s.table.put("x", list);
		//s.lookup("x");
		System.out.println(s.lookup("x"));
		
	}*/
	


}
