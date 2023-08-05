package com.example.healthapp.ui.dashboard;

import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthapp.databinding.FragmentDashboardBinding;
import com.example.healthapp.ui.home.UserClass;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DashboardFragment extends Fragment {
    List<UserClass> userClassList, userClassListTEst, userClassTestDummy;
    List<UserClass> userClassTest2 = new ArrayList<>();
    private FragmentDashboardBinding binding;
    BarChart barChart;
    List<BarEntry> barEntries = new ArrayList<>();

    LineChart lineChart, lineChart2;

    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> items;
    private SparseBooleanArray selectedItems;
    List<String> selectdItems2 = new ArrayList<>();
    List<Integer> monthSelection = new ArrayList<>();
    boolean switchStatus = true;
    PieChart pieChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textDashboard;
        // dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        addData();
        addData2();
        dashboardViewModel.getUserDataFromFireBase(new DashboardViewModel.OnDataRetrievedListener() {
            @Override
            public void onDataRetrieved(List<UserClass> data) {
                userClassList = data;

                if (!userClassList.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (UserClass user : userClassList) {
                        stringBuilder.append(user.getName()).append("\n");
                        System.out.println(user.getDate());
                    }
//                    binding.textDashboard.setText(stringBuilder.toString());
                    System.out.println("SIZE OF USERCLASS IS " + userClassList.size());

                    binding.switch1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(switchStatus){
                                switchStatus=false;
                            }else{
                                switchStatus=true;
                            }
                            if(switchStatus){
                                createLineChart(userClassList);
                            }else {
                                createLineChart2(userClassList);
                            }

                            setSpinner();

                        }
                    });
                    if(switchStatus){
                        createLineChart(userClassList);
                    }else {
                        createLineChart2(userClassList);
                    }
//                    createLineChart2(userClassList);
                    createPieChart(userClassList);
                    getMonths(userClassList);
                    setSpinner();

                } else {
//                    binding.textDashboard.setText("No user data available.");
                }
            }
        });

        return root;
    }

    public void getMonths(List<UserClass> userClassListTEst){

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
        for (UserClass user: userClassListTEst
             ) {
            try{
                Date date = dateFormat.parse(user.getDate());
                if(monthSelection.contains(date.getMonth())){
                    monthSelection.remove(date.getMonth());
                }else{
                    monthSelection.add(date.getMonth());
                }
            }catch (Exception e){

            }

        }
        for (int i: monthSelection
             ) {
            System.out.println(i);
        }
    }

    private void createLineChart(List<UserClass> userClassListTEst) {
        System.out.println(userClassListTEst.size()+" is the size of the array the chart got...");

        lineChart = binding.chartLine;
        lineChart2 = binding.chartLine;
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        //sort the userClassList
        Collections.sort(userClassListTEst, new Comparator<UserClass>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");

            @Override
            public int compare(UserClass u1, UserClass u2) {
                try {
                    Date date1 = dateFormat.parse(u1.getDate());
                    Date date2 = dateFormat.parse(u2.getDate());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");


        //get weight data
        for (int i = 0; i < userClassListTEst.size(); i++) {
            float x = i;
            String height = userClassListTEst.get(i).getWeight();
            if (height != null) {
                float y = Float.parseFloat(height);
                entries.add(new Entry(x, y));
            }
        }

        //get target Weight data
        for (int i = 0; i < userClassListTEst.size(); i++) {
            float x = i;
            String taget = userClassListTEst.get(i).getHeight();
            if (taget != null) {
                float y = Float.parseFloat(taget);
                entries2.add(new Entry(x, y));
            }
        }




        LineDataSet dataSet = new LineDataSet(entries, "Weight");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
//        LineData lineData = new LineData(dataSet);

        LineDataSet dataSet2 = new LineDataSet(entries2, "Target");
        dataSet2.setColor(Color.RED);
        dataSet2.setCircleColor(Color.RED);
        dataSet2.setLineWidth(2f);

        LineData lineData = new LineData(dataSet, dataSet2);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Minimum axis-step (interval) is 1
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                if (index >= 0 && index < userClassListTEst.size()) {

                    String regex = "\\s(.*?)\\s";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(userClassListTEst.get(index).getDate());
                    if (matcher.find()) {
                        String result = matcher.group(1);
                        return result;}

//                    return userClassListTEst.get(index).getDate();

                }
                return "";
            }
        });



        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setGranularity(1f); // Minimum axis-step (interval) is 1

        lineChart.getDescription().setEnabled(true); // Hide chart description
        lineChart.getLegend().setEnabled(true); // Hide legend
        System.out.println(lineData.toString()+" is the line data");
        lineChart.setData(lineData);
//        lineChart.setData(lineData2);
        lineChart.invalidate(); // Refresh the chart
    }

    private void createLineChart2(List<UserClass> userClassListTEst) {
        lineChart = binding.chartLine;
        lineChart2 = binding.chartLine;
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        //sort the userClassList
        Collections.sort(userClassListTEst, new Comparator<UserClass>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");

            @Override
            public int compare(UserClass u1, UserClass u2) {
                try {
                    Date date1 = dateFormat.parse(u1.getDate());
                    Date date2 = dateFormat.parse(u2.getDate());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");


        //get chest data
        for (int i = 0; i < userClassListTEst.size(); i++) {
            float x = i;
            String chest = userClassListTEst.get(i).getChest();
            if (chest != null) {
                float y = Float.parseFloat(chest);
                entries.add(new Entry(x, y));
            }
        }

        //get target back data
        for (int i = 0; i < userClassListTEst.size(); i++) {
            float x = i;
            String back = userClassListTEst.get(i).getBack();
            if (back != null) {
                float y = Float.parseFloat(back);
                entries2.add(new Entry(x, y));
            }
        }






        LineDataSet dataSet = new LineDataSet(entries, "Chest");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
//        LineData lineData = new LineData(dataSet);

        LineDataSet dataSet2 = new LineDataSet(entries2, "Back");
        dataSet2.setColor(Color.RED);
        dataSet2.setCircleColor(Color.RED);
        dataSet2.setLineWidth(2f);

        LineData lineData = new LineData(dataSet, dataSet2);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Minimum axis-step (interval) is 1
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                if (index >= 0 && index < userClassListTEst.size()) {

                    String regex = "\\s(.*?)\\s";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(userClassListTEst.get(index).getDate());
                    if (matcher.find()) {
                        String result = matcher.group(1);
                        return result;}

//                    return userClassListTEst.get(index).getDate();

                }
                return "";
            }
        });



        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setGranularity(1f); // Minimum axis-step (interval) is 1

        lineChart.getDescription().setEnabled(true); // Hide chart description
        lineChart.getLegend().setEnabled(true); // Hide legend
        lineChart.setData(lineData);
//        lineChart.setData(lineData2);
        lineChart.invalidate(); // Refresh the chart
    }

    public void addData(){
        userClassListTEst = new ArrayList<>();
        userClassListTEst.add(new UserClass("name1", "uid", "10", "10",  "Sat Jul 1 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "10", "10",  "Sat Jul 2 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "10", "10",  "Sat Jul 3 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "10", "10",  "Sat Jul 4 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "10", "10",  "Sat Jul 5 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "10", "10",  "Sat Jul 6 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "10", "10",  "Sat Jul 7 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "10", "10",  "Sat Jul 8 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "10", "10",  "Sat Jul 9 09:50:55 PDT 2023"));

        userClassListTEst.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 21 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 22 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 23 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 24 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 25 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 26 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 27 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 28 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 29 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 30 09:50:55 PDT 2023"));

        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 10 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 11 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 12 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 13 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 14 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 15 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 16 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 17 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 18 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 19 09:50:55 PDT 2023"));
        userClassListTEst.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 20 09:50:55 PDT 2023"));

    }

    public void addData2(){
        userClassTestDummy = new ArrayList<>();


        userClassTestDummy.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 21 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 22 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 23 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 24 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 25 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 26 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 27 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 28 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 29 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "20", "10",  "Sat Sep 30 09:50:55 PDT 2023"));

        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 10 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 11 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 12 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 13 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 14 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 15 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 16 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 17 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 18 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 19 09:50:55 PDT 2023"));
        userClassTestDummy.add(new UserClass("name1", "uid", "0", "10",  "Sat Aug 20 09:50:55 PDT 2023"));

    }

    public void setSpinner(){

        items = new ArrayList<>();
        selectedItems = new SparseBooleanArray();

        for (int x: monthSelection
             ) {
            switch (x){
                case 0: items.add("Jan");break;
                case 1: items.add("Feb");break;
                case 2: items.add("Mar");break;
                case 3: items.add("Apr");break;
                case 4: items.add("May");break;
                case 5: items.add("Jun");break;
                case 6: items.add("Jul");break;
                case 7: items.add("Aug");break;
                case 8: items.add("Sep");break;
                case 9: items.add("Oct");break;
                case 10: items.add("Nov");break;
                case 11: items.add("Dec");break;
                default:break;
            }
        }
        spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(spinnerAdapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toggle the selection state of the item
                String text="";
                List<UserClass> updatedList = new ArrayList<>();
                boolean isSelected = selectedItems.get(position);
                selectedItems.put(binding.spinner.getSelectedItemPosition(), !isSelected);
//                for(int i = 0 ; i<selectedItems.size(); i++){
//
//                }

                if(selectdItems2.contains(binding.spinner.getSelectedItem())){
                    int x = selectdItems2.indexOf(binding.spinner.getSelectedItem());
                    selectdItems2.remove(x);
                    for (String j: selectdItems2
                    ) {
                        text = text +" "+ j;
                    }
                }else{
                    selectdItems2.add(binding.spinner.getSelectedItem().toString());
                    for (String x: selectdItems2
                         ) {
                        text = text +" "+ x;
                    }

                }
                if(switchStatus){
                    binding.textView.setText(" FOOD data for the months of : " + "\n" +text);
                }else{
                    binding.textView.setText(" Exercise data for the months of : " + "\n" +text);
                }


                for (String month: selectdItems2
             ) {
            try{
                for (UserClass user: userClassList
                     ) {
                    String regex = "\\s(.*?)\\s";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(user.getDate());
                    if (matcher.find()) {
                        String result = matcher.group(1);
//                        System.out.println(result);
                        if (result.equals(month)){
                           // new array here
                            updatedList.add(user);

                        }
                    }

                }

            }catch (Exception e){

            }
        }
                System.out.println("From Spinner size of array to be used is "+updatedList.size());
                System.out.println("From Spinner size of array selectdItems2 to be used is "+selectdItems2.size());
                if(switchStatus){
                    createLineChart(updatedList);
                }else {
                    createLineChart2(updatedList);
                }
                createPieChart(updatedList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when no item is selected
            }
        });
    }

    public void createPieChart(List<UserClass> listOfUserClass){
        pieChart = binding.pieChart;
        ArrayList<PieEntry> entries = new ArrayList<>();

        float carbsSum = 0f;
        float fatsSum = 0f;
        float proteinSum = 0f;

        for (UserClass user:listOfUserClass
             ) {
            carbsSum += Float.parseFloat(user.getCarbs().toString());
            fatsSum += Float.parseFloat(user.getFats().toString());
            proteinSum += Float.parseFloat(user.getProtein().toString());
        }

        entries.add(new PieEntry(carbsSum, "Carbs"));
        entries.add(new PieEntry(fatsSum, "Fats"));
        entries.add(new PieEntry(proteinSum, "Protiens"));

        PieDataSet dataSet = new PieDataSet(entries, "Macro Distribution");
        dataSet.setColors(Color.GREEN, Color.RED, Color.GRAY); // Customize colors
        dataSet.setValueTextSize(12f); // Customize value text size

        PieData pieData = new PieData(dataSet);

        pieChart.setUsePercentValues(true); // Show percentage values
        pieChart.setEntryLabelColor(Color.BLACK); // Customize label color
        pieChart.setData(pieData);

        pieChart.invalidate();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
