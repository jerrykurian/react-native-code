import React, { Component } from "react";
import { Image, View, AsyncStorage } from "react-native";
import { connect } from "react-redux";
import { DeviceEventEmitter } from "react-native";
import {
  Container,
  Header,
  Title,
  Content,
  Text,
  Button,
  Left,
  Right,
  Body,
  Icon,
  Card,
  CardItem,
  ListItem,
  List,
  Picker,
  Item,
  Input
} from "native-base";
import styles from "./style";
import { PermissionsAndroid } from "react-native";
import Spinner from "react-native-loading-spinner-overlay";
const commonColor = require("../../theme/variables/commonColor");
const glow2 = require("../../../assets/glow2.png");
var deviceAttachedSubscription, deviceRemovedSubscription, deviceFailedSubscription;
import FPScanBridge from "../../bridge/fpScanNativeModule";
import locservice from "../../location/locations";
class Scan extends Component {
  constructor(props) {
      super(props);
      this.state = {
        userName: "",
        userId: "",
        masterId: "",
        buckleId: "",
        profileImage: "",
        unitName: "",
        userLoaded:false,
        latitude:"-1",
        longitude:"-1",
        allowedLatitude:-1,
        allowedLongitude: -1,
        lastAttendanceId:"",
        locationFound: false,
        locationId: -1,
        locationName: "",
        error:"",
        message:"",
        locationSet: false,
        userRecorded: false,
        userFound:"",
        userNotFound: false,
        withinGeoFence : false,
        locationCheck: false,
        profileImage: "https://www.engineergirl.org/images/person/empty-person.png"
      };
  }
  // Will be called when the screen is opened
  componentWillMount() {
    try {
      // Set the locations to the current location
      this.setState({"locations": [{name:"Please select the duty location", id:"-1"}],
        loadingLocations:true});
      locservice.loadLocations(
        (responseJson)=>{
          AsyncStorage.getItem("location_id").then((value) => {
              responseJson.map(loc=>{
                if (loc.id == value){
                  this.setState({"location": loc, "locationName":loc.name,
                    "locationId": value, "locationSet":true,
                    allowedLatitude: loc.latitude, allowedLongitude: loc.longitude,
                    loadingLocations:false});
                  FPScanBridge.init(loc.id.toString(),()=>{});
                }
              });
              this.setState({"locations": responseJson, loadingLocations:false});
          });
        },
        (error) =>{
          this.setState({loadingLocations:false});
          alert("Unable to load location, please try again");
        },
        "Please select the duty location"
      );
	    FPScanBridge.isConnected((conn)=>{
	      // Set the connected to the state of the device
	      this.setState({connected: conn});
	    });
    } catch (error){
      this.state.error = error;
    }
  }
  setMessage(msg){
    this.state.message = msg;
  }
  checkGeoFence(lat,long){
    if (this.state.allowedLatitude == -1 || this.state.allowedLongitude == -1){
      return false;
    }
    var distance = this.calculateDistance(lat,long,this.state.allowedLatitude,
      this.state.allowedLongitude, "me");
    if (distance <= 50){
      this.setMessage("Person within boundary");
      return true;
    } else {
      this.setMessage("Person outside boundary");
      return false;
    }
  }
  calculateDistance (lat1, lon1, lat2, lon2, unit) {
    var radlat1 = Math.PI * lat1 / 180;
    var radlat2 = Math.PI * lat2 / 180;
    var radlon1 = Math.PI * lon1 / 180;
    var radlon2 = Math.PI * lon2 / 180;
    var theta = lon1 - lon2;
    var radtheta = Math.PI * theta / 180;
    var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) *
      Math.cos(radlat2) * Math.cos(radtheta);
    dist = Math.acos(dist);
    dist = dist * 180 / Math.PI;
    dist = dist * 60 * 1.1515;
    if (unit == "K") {
      dist = dist * 1.609344;
    }
    if (unit == "me") {
      dist = dist * 1.609344 * 1000;
    }
    if (unit == "N") {
      dist = dist * 0.8684;
    }
    return dist;
  }
  checkValidityOfLocation(){
    this.findGeoLocation(
      (lat,long)=>{
        this.verifyFencing(lat,long);
      },
      (error)=>{
        alert("Unable to locate you. Please try again before marking attendance");
        this.setState({locationCheck:false, withinGeoFence:false});
      }
    );
  }
  verifyFencing(lat,long){
    if (this.checkGeoFence(lat,long)){
      this.setState({locationCheck:false, withinGeoFence:true,
       locationFound:true, latitude:lat, longitude:long});
    } else {
      alert("Current position is beyond allowed limits of the chosen location.");
      this.setState({locationCheck:false, withinGeoFence:false});
    }
  }
  componentDidMount(){
    try {
      this.checkValidityOfLocation();
      // Add listener to react to device detached events
      this.deviceRemovedSubscription = DeviceEventEmitter.addListener("DEVICEDETACHED", (data) => {
          this.setState({connected: false});
        });
      // Add listener to react to device attached events
      this.deviceAttachedSubscription = DeviceEventEmitter.addListener("DEVICEATTACHED", (data) => {
          this.setState({connected: true});
        });
      // Add listener to react to device failed events
      this.deviceFailedSubscription = DeviceEventEmitter.addListener("DEVICEFAILED", (data) => {
            this.setState({connected: false});
        });
      this.serverCalledSubscription = DeviceEventEmitter.addListener("SERVERCALLED", (data) => {
          this.setState({serverCalled: true});
        });
      this.serverReturnedSubscription = DeviceEventEmitter.addListener("SERVERRETURNED", (data) => {
          this.setState({serverCalled: false});
        });
    } catch (error){
      this.state.error = error;
    }
  }
  listUsers(){
    if (this.state.users.length > 0){
      return (
        <Content padder>
          <List
            dataArray={this.state.users}
            renderRow={(
              data // eslint-disable-line
            ) =>
              <ListItem icon style={styles.listitem} button onPress={() =>
                {this.onSelect(data);}}>
                <Body>
                  <Text>
                    {data.name} ({data.buckleId})
                  </Text>
                </Body>
              </ListItem>}
          />
        </Content>
        );
    } else {
      return (
        <Content padder>
          <Body>
            <Text>
              No Users Found
            </Text>
          </Body>
        </Content>
        );
    }
  }
  async findGeoLocation(currentPosSuc, currentPosFailure){
    try {
        this.setState({locationCheck:true});
        const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
            {
                "title": "Location Permission",
                "message": "Geoattend needs to access your location."
            }
        );
        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
          navigator.geolocation.getCurrentPosition(
            (position) => {
              currentPosSuc(position.coords.latitude, position.coords.longitude);
            },
            (error) => {currentPosFailure(error);},
            { enableHighAccuracy: true, timeout: 100000, maximumAge: 1000 },
          );
          if (this.watchId == null){
            this.watchId = navigator.geolocation.watchPosition(
              (position) => {
                this.setState({latitude: position.coords.latitude,
                    longitude: position.coords.longitude});
                this.verifyFencing(position.coords.latitude,
                position.coords.longitude);
              },
              (error) => this.setState({ error: error.message }),
              { enableHighAccuracy: true, timeout: 20000, maximumAge: 1000, distanceFilter: 1 }
            );
          }
        } else {
            currentPosFailure("Location permission denied");
        }
    } catch (err) {
        console.warn(err);
        currentPosFailure("Location permission denied " + err);
    }
  }
  loadUser(){
    this.setState({userNotFound:false});
  	try {
	    if (this.state.location.id == -1){
	      alert("Please set duty location");
	      return;
	    }
	    if (this.state.enteredUser === ""){
	      alert("Please specify name");
	      return;
	    }
      this.searchUser();
	  }
	  catch (error){
	  	alert("Sorry something went wrong " + error);
    }
  }
  searchUser(){
    this.setState({userRecorded: false});
    let text = this.state.enteredUser;
    this.setState({userLoaded:false, loadingUsers: true});
    var fetchServicesUri = "http://tfpapi.learnym.com/api/users?loc_id=" + this.state.location.id + "&name=" + text;
    fetch(fetchServicesUri)
        .then((response) => response.json())
        .then((responseJson) => {
          this.setState({ users: responseJson, userListed:true, loadingUsers: false});
      })
      .catch((error) => {
        this.setState({loadingUsers: false});
        alert("Sorry, unable to load users");
      }).done();
  }
  // Called if a user changes location
  updateLocation(loc){
  	try {
	    if (loc.id.toString() !== -1){
	      this.setState({"location": loc, "locationId":loc.id.toString(),
	        "locationName":loc.name, "locationSet":true,
          allowedLatitude: loc.latitude, alloswedLongitude: loc.longitude
        });
        this.checkValidityOfLocation();
	      AsyncStorage.setItem("location_id", loc.id.toString());
	      AsyncStorage.setItem("location_name", loc.name);
        FPScanBridge.init(loc.id.toString(),()=>{});
	    } else {
	      this.setState({"locationSet":true, "message":"Location not set"});
	    }
	  } catch (error){alert("Sorry something went wrong " + error);}
  }
  componentWillUnmount(){
    // Remove the listeners and stop the scanner once the screen
    // goes out of view
    try {
	    this.deviceAttachedSubscription.remove();
	    this.deviceRemovedSubscription.remove();
	    this.deviceFailedSubscription.remove();
	    this.serverCalledSubscription.remove();
	    this.serverReturnedSubscription.remove();
	    FPScanBridge.stopCapture();
      if (this.watchId !== null){
        navigator.geolocation.clearWatch(this.watchId);
      }
		} catch (error){alert("Sorry something went wrong " + error);}
  }
  onSelect(value){
    this.setState({users:[],userName: value.name, buckleId: value.buckleId, unitName: value.unitName, userLoaded:true, userListed:false,userId: value.id.toString()});
  }
  // Call to start finger print verification
  async startVerification(){
    try {
      // This will be an async call to start waiting for the finger print
      // authentication events
      var {result, userId, scanId, buckleId, masterId, userName, unitName, message, errorCode} = await FPScanBridge.userAuthenticate(this.state.userId.toString);
      if (result)
        {this.setState({score: message, userNotFound:false});}
      else
        {this.setState({userNotFound:false});}
      this.recordAttendance(this.state.userId, scanId, result);
    } catch (error){
      this.setState({error:error, serverCalled:false});
    }
  }
  async recordAttendance(userId, scanId, sresult){
    try {
      var {result, message, attendanceId} = await FPScanBridge.recordAttendance(userId,
          this.state.latitude.toString(),
          this.state.longitude.toString(),
          this.state.locationId, scanId, sresult);
      if (result){
        this.setState({userRecorded: true, userLoaded:false});
        this.setState({lastAttendanceId: attendanceId});
        this.setState({error: message, serverCalled:false});
        FPScanBridge.recordAttendanceResult(userId, attendanceId);
      } else {
        alert("Unable to save the data. Please try again");
      }
    } catch (error){
      this.setState({error:error, serverCalled:false});
    }
  }
  startScan(){
    if (this.state.withinGeoFence){
    	try {
        // Check if the device is connected at the screen start
        FPScanBridge.isConnected((conn)=>{
          // Set the connected to the state of the device
          if (!conn){
            // If not then show message to connect device
            alert("Scanner device is not connected. Please connect to mark attendance");
          } else {
            // If connected, the start scanning
            if (this.state.locationSet){
              this.setMessage("");
              this.startVerification();
            } else {
              this.setMessage("Please select the location before marking attendance");
            }
          }
        });
       } catch (error){alert("Sorry, something went wrong " + error);}
     } else {
      alert("You are not allowed to mark attendance from this location");
     }
  }
  noUserInfo(){
    if (this.state.userRecorded){
      return (
        <Card
          style={styles.box}>
          <View style={styles.cardView}>
            <CardItem bordered style={styles.cardItem}>
              <Text
                style={{
                  color: commonColor.inverseTextColor,
                  paddingLeft: 10
                }}
              >
                Attendance Recorded.
              </Text>
            </CardItem>
          </View>
        </Card>
        );
    } else {
      return (
        <Card
          style={styles.box}>
          <View style={styles.cardView}>
            <CardItem bordered style={styles.cardItem}>
              { this.state.userNotFound ?
               (
                <View>
                  <Text
                  style={{
                    color: commonColor.inverseTextColor,
                    paddingLeft: 10
                  }}
                  >
                    Unable to find user.
                  </Text>
                  <Button
                      light
                      rounded
                      block
                      style={{ marginTop: 5 }}
                      onPress={() => this.recordAttendance(this.state.userId,null,false)}
                    ><Text style={{ color: commonColor.btnDangerColor }}>
                      Manual Verification Done. Record Attendance
                    </Text>
                  </Button>
                 </View>
                ) : (
                  <Text
                  style={{
                    color: commonColor.inverseTextColor,
                    paddingLeft: 10
                  }}
                  >
                    Search and find user
                  </Text>
                )
              }

            </CardItem>
          </View>
        </Card>
      );
    }
  }
  userInfo(){
    const { userName, buckleId, unitName } = this.state;
    return (
      <View  padder>
       <Card
        style={styles.box}>
         <List>
          <ListItem icon style={styles.listitem}>
            <Left>
              <Text style={{ width: 100, fontSize: 15, color: commonColor.lightTextColor }}>Name</Text>
            </Left>
            <Body>
              <Text
                style={{
                  fontSize: 15,
                  color: commonColor.inverseTextColor,
                  paddingLeft: 10
                }}
              >
                {userName}
              </Text>
            </Body>
          </ListItem>
          <ListItem icon style={styles.listitem}>
            <Left>
              <Text style={{ width: 100, fontSize: 15, color: commonColor.lightTextColor }}>Buckle Id</Text>
            </Left>
            <Body>
              <Text
                style={{
                  fontSize: 20,
                  color: commonColor.inverseTextColor,
                  paddingLeft: 10
                }}
              >
                {buckleId}
              </Text>
            </Body>
          </ListItem>
          <ListItem icon style={styles.listitem}>
            <Left>
              <Text style={{ width: 100, fontSize: 15, color: commonColor.lightTextColor }}>Unit Name</Text>
            </Left>
            <Body>
              <Text
                style={{
                  fontSize: 15,
                  color: commonColor.inverseTextColor,
                  paddingLeft: 10
                }}
              >
                {unitName}
              </Text>
            </Body>
          </ListItem>
        </List>
       </Card>
       <Button
          light
          rounded
          block
          style={{ marginTop: 20 }}
          onPress={() => this.startScan()}
        >
          <Text style={{ color: commonColor.brandSuccess }}>
            Mark Attendance
          </Text>
        </Button>
      </View>
    );
  }
  render() {
    const navigation = this.props.navigation;
    return (
      <Container>
        <Image source={glow2} style={styles.containerImage}>
          <Header>
            <Left>
              <Button
                transparent
                onPress={() => navigation.navigate("DrawerOpen")}
              >
                <Icon active name="menu" />
              </Button>
            </Left>
            <Body>
              <Title>Record</Title>
            </Body>
            <Right />
          </Header>
          <Content padder style={{ paddingTop: 0 }}>
            <View padder
              style={styles.footer}>
                {this.state.connected ? (
                   <Text style={{
                        fontSize: 12, fontWeight: "bold",
                        color: commonColor.brandSuccess }}>
                      Device connnected. Please record attendance.
                    </Text>
                    ) : (
                     <Text style={{fontSize: 12, fontWeight: "bold",
                     color: commonColor.brandDanger }}>
                      Device not connnected. Please connect for attendance.
                    </Text>
                  )
                }
                {this.state.locationFound ? (
                     <Text style={{
                        fontSize: 12, fontWeight: "bold",
                        color: commonColor.brandSuccess }}>
                      Location : [{this.state.locationName}] - [{this.state.latitude}, {this.state.longitude}]
                    </Text>
                    ) : (
                     <Text style={{fontSize: 12, fontWeight: "bold",
                     color: commonColor.brandDanger }}>
                      Location : [{this.state.locationName}]
                    </Text>
                  )
                }
                <Text style={{
                        fontSize: 12, fontWeight: "bold",
                        color: commonColor.brandDanger }}>
                      Messages : [{this.state.error}]
                    </Text>
                <Picker
                    style={{ color: commonColor.brandPrimary }}
                    itemStyle={styles.items}
                    selectedValue={this.state.location}
                    onValueChange={(loc) => this.updateLocation(loc)}>
                        {this.state.locations.map((l, i) => {return <Picker.Item value={l} label={l.name} key={i}  />; })}
                </Picker>
            </View>
            <View padder>
              <Item underline>
                <Icon active name="person" />
                <Input
                  placeholder="Name"
                  placeholderTextColor="rgba(230,230,230,0.8)"
                  onChangeText={(text) => this.setState({ enteredUser: text })}
                  style={{ color: "#fff", width: 100 }}
                />
                <Button
                  light
                  rounded
                  block
                  style={{ marginTop: 5 }}
                  onPress={() => this.loadUser()}
                ><Text style={{ color: commonColor.brandPrimary }}>
                  Search
                </Text>
              </Button>
              </Item>
              {this.state.userListed ? (
                this.listUsers()
                ) : (
                <Text style={{ color: commonColor.brandPrimary }} />
                )
              }
                {this.state.userLoaded ? (
                     this.userInfo()
                    ) : (
                     this.noUserInfo()
                    )
                }
            </View>
            <Spinner visible={this.state.loadingUsers} textContent={"Loading Users..."} textStyle={{color: "#FFF"}} />
            <Spinner visible={this.state.serverCalled} textContent={"Accessing Server..."} textStyle={{color: "#FFF"}} cancelable={true} />
            <Spinner visible={this.state.locationCheck} textContent={"Verifying Location..."} textStyle={{color: "#FFF"}} cancelable={true} />

            <Spinner visible={this.state.loadingLocations} textContent={"Loading Locations..."} textStyle={{color: "#FFF"}} cancelable={true} />
          </Content>
        </Image>
      </Container>
    );
  }
}

export default connect()(Scan);
