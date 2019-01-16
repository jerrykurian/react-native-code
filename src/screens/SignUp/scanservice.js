import FPScanBridge from "../../bridge/fpScanNativeModule";
export default {
  startScan(state, started, ended){
    const { userId, userName, buckleId, unitName, location } = state;
    try {
      FPScanBridge.isConnected((conn)=>{
        try {
          if (conn){
            started();
            FPScanBridge.register(userId, userName, buckleId, unitName, location.name.toString(),
              (scanned, code, message)=>{
                if (scanned){
                  alert("Finger Scanned and Registered. " + code + ". Press Ok to continue.");
                } else {
                  alert("Could not scan finger" + message + ". Please try again.");
                }
                ended();
              }
            );
          } else {
            alert("Device is not connected. Please connect to start scanning.");
          }
        } catch (error){
          alert("Unable to register " + error);
          ended();
        }
      });
    } catch (error){
      ended();
      alert("Unable to register " + error);
    }
  }
};
