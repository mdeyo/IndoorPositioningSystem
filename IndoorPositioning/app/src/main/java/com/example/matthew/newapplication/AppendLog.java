package com.example.matthew.newapplication;

import android.os.AsyncTask;

/**
 * Created by Matthew on 7/14/2015.
 */
public class AppendLog extends AsyncTask<GridData,Integer,Integer> {
    // Do the long-running work in here
    RunMode main;
    int[] numbers = new int[240];

    protected void setRunMode(RunMode r){
        this.main = r;
    }
    protected Integer doInBackground(GridData...strings) {
        int count = strings.length;
        long totalSize = 0;
        for (int i = 0; i < count; i++) {
            GridData grid = strings[i];
            main.newLocation(grid.printFullLocation());

            //re-initialize image mapping to all be empty
            for (int n = 0; n < numbers.length; n++) {
                numbers[n] = -1;
            }

            numbers[grid.getPosition()] = 0;

            main.updateGrid(numbers);

            main.buildingnumber = grid.getBuilding();
            main.floornumber = grid.getFloor();
            main.currentPosition = grid.getPosition();

            main.updateMapView();

//            totalSize += Downloader.downloadFile(urls[i]);
//            publishProgress((int) ((i / (float) count) * 100));
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return (int)totalSize;
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(Long result) {
//        showNotification("Downloaded " + result + " bytes");
    }
}