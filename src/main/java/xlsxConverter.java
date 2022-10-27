import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class xlsxConverter {

    public static void converter(String filepathNoExtension) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheetAddress = workbook.createSheet("Dedicated to Nastya Shamrikova from 20BI-2.");
        XSSFRow row;
        String reader = "";
        int temp = 0;
        String comma = ",";
        int counter = 0;
        List<String> lst = new ArrayList<>();

        try{
            BufferedReader buffer = new BufferedReader(new FileReader(filepathNoExtension+".csv"));
            while((reader = buffer.readLine()) != null){
                String[] arr = reader.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                lst.addAll(Arrays.asList(arr));
                int i = 0;
                for (i = i; i<1; i++ ) {
                    String[] tempArr = reader.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    counter = tempArr.length;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        for(int i=0; i<lst.size()/counter;i++){
            row = sheetAddress.createRow(i);
            for(int j=0;j<counter;j++){
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(lst.get(temp));
                if(temp == lst.size()-1){
                    break;
                }
                else{
                    temp++;
                }
            }
        }
        try{
            FileOutputStream fos = new FileOutputStream(new File(filepathNoExtension+".xlsx"));
            workbook.write(fos);
            fos.close();
//            System.out.println("The file is converted!");
        } catch (IOException ignored) {
        }
    }
}
