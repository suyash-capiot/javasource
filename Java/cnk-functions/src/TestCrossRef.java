import com.cnk.travelogix.util.CNKFunctions;
import com.cnk.travelogix.util.CrossRef;

public class TestCrossRef {

	public static void main(String[] args) {
		System.out.println("Start Time: <" + System.currentTimeMillis() + ">");
		System.out.println(CNKFunctions.crossRefLookup("ezeego1", "country", "IN", "TLGX"));
		System.out.println("Middle Time: <" + System.currentTimeMillis() + ">");
		System.out.println(CrossRef.lookup("iween", "city", "Pune, India", "TLGX"));
		System.out.println("End Time: <" + System.currentTimeMillis() + ">");
		System.exit(0);
	}

}
