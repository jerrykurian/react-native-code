package com.learnym.scan.datastore;

import com.learnym.scan.api.model.Location;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Jerry Kurian on 29-11-2017.
 */

public class LocationManager {
    static Location chosenLocation;

    public static List<Location> loadAllLocations() throws IOException {
        Call<List<Location>> locationCall = FPDataStore.locationService.loadAll();
        Response<List<Location>> res = locationCall.execute();
        if(res.isSuccessful()){
            return res.body();
        }else{
            throw new IOException(res.message());
        }
    }

    public static void setChosenLocation(Location loc){
        chosenLocation = loc;
    }

    public Location getChosenLocation(){
        return chosenLocation;
    }
}
