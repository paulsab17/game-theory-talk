package backgroundClasses;

public class Helper {
	public static int randMinMax(int min, int max){
		return (int)((max-min+1)*Math.random())+min;
	}
	public static double trunc(double d){
		double res = (int)(d*100);
		res/=100.0;
		return res;
	}
	public static int getJailTime(String a,String b){
		boolean aBetrayed,bBetrayed;
		if(a.equals("betray")){
			aBetrayed = true;
		}else if(a.equals("silent")){
			aBetrayed = false;
		}else{
			throw new IllegalArgumentException();
		}
		if(b.equals("betray")){
			bBetrayed = true;
		}else if(b.equals("silent")){
			bBetrayed = false;
		}else{
			throw new IllegalArgumentException();
		}
		
		if(aBetrayed && bBetrayed){
			return 2;
		}else if(aBetrayed && !bBetrayed){
			return 0;
		}else if(!aBetrayed && bBetrayed){
			return 3;
		}else{
			return 1;
		}
	}
	public static int getDeerFood(String a,String b){
		boolean aDeer,bDeer;
		if(a.equals("deer")){
			aDeer = true;
		}else if(a.equals("hare")){
			aDeer = false;
		}else{
			throw new IllegalArgumentException();
		}
		if(b.equals("deer")){
			bDeer = true;
		}else if(b.equals("hare")){
			bDeer = false;
		}else{
			throw new IllegalArgumentException();
		}
		
		if(aDeer && bDeer){
			return 2;
		}else if(aDeer && !bDeer){
			return 0;
		}else if(!aDeer && bDeer){
			return 1;
		}else{
			return 1;
		}
	}
}
