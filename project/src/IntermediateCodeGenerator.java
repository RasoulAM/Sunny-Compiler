/**
 * Created by Rasoul on 1/26/2018.
 */
public class IntermediateCodeGenerator {


    String[] programBlock = new String[100];
    int index = 0;
    int baseTempAddress = 500;

    public int getTemp(){
        int temp = baseTempAddress;
        baseTempAddress += 4;
        return temp;
    }


}
