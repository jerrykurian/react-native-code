import React, { Component } from "react";
import { Image } from "react-native";
import { connect } from "react-redux";
import { DeviceEventEmitter, AsyncStorage  } from "react-native";
import {
  Container,
  Header,
  Title,
  Content,
  Button,
  Icon,
  Item,
  Input,
  View,
  Text,
  Left,
  Body,
  Right,
  Card,
  CardItem,
  ListItem,
  List,
  Picker,
  TouchableOpacity
} from "native-base";
import styles from "./styles";
import scanner from "./scanservice";
import FPScanBridge from "../../bridge/fpScanNativeModule";
import Spinner from "react-native-loading-spinner-overlay";
import locservice from "../../location/locations";
const commonColor = require("../../theme/variables/commonColor");
const glow2 = require("../../../assets/glow2.png");

class SignUp extends Component {
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
      userListed:false,
      savingScan:false
    };
  }
  loadLocations(){
    this.setState({"location": {}});
    this.setState({"locations": [{name:"Set Duty Location", id:"-1"}]});
    this.setState({"loadingData": true});
    this.setState({"locationSet": false});
    this.setState({"location_id": -1});
    locservice.loadLocations(
      (responseJson)=>{
        this.setState({"locations": responseJson});
        this.setState({"loadingData": false});
      },
      (error) =>{
        this.setState({"loadingData": false});
        alert("Unable to load location, please try again");
      },
      "Set Duty Location"
    );
  }
  componentWillMount(){
    try {
      this.loadLocations();
      this.setState({"loadingUnits": true,
        "loadingUsers": false, "savingScan" : false});
      this.setState({"unit": {}});
      this.setState({"units": [{name:"Please Select", id:"-1"}]});
      this.setState({"users":[]});
      var fetchServicesUri = "http://tfpapi.learnym.com/api/units";
      fetch(fetchServicesUri)
        .then((response) => response.json())
        .then((responseJson) => {
          this.setState({"loadingUnits":false});
          responseJson.unshift({name:"Select Unit", id:-1});
          this.setState({"units": responseJson});
        })
        .catch((error) => {
            this.setState({"loadingUnits":false});
            alert("Sorry, unable to load units. Please try again.");
        }).done();
    } catch (error){
      this.setState({"loadingUnits":false});
      alert("Sorry, we encountered problem while initialializing. Please try again.");
    }
  }
  // Called when user changes the current location from drop down
  updateLocation(loc){
    if (loc.id.toString() !== -1){
      this.setState({location: loc});
    } else {
      this.setState({location: loc, locationSet: false});
    }
  }
  componentDidMount(){
    this.deviceRemovedSubscription = DeviceEventEmitter.addListener("SERVERCALLED", (data) => {
        this.setState({serverCalled: true});
      });
    this.deviceRemovedSubscription = DeviceEventEmitter.addListener("SERVERRETURNED", (data) => {
        this.setState({serverCalled: false});
      });

  }
  updateUnit(unit){
    this.setState({"unit": unit});
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
  loadUser(){
    if (this.state.location.id == -1){
      alert("Please set duty location");
      return;
    }
    if (this.state.unit.name === "Select Unit"){
      alert("Please select a unit");
      return;
    }
    if (this.state.enteredUser === ""){
      alert("Please specify name");
      return;
    }
    let text = this.state.enteredUser;
    this.setState({userLoaded:false, loadingUsers: true});
    var fetchServicesUri = "http://tfpapi.learnym.com/api/users?unit_name=" + this.state.unit.name + "&name=" + text;

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
  onSelect(value){
    this.setState({users:[],userName: value.name, buckleId: value.buckleId, unitName: value.unitName, userLoaded:true, userListed:false,userId: value.id.toString()});
  }
  startScan(){
    scanner.startScan(this.state,
      ()=>{this.setState({savingScan:true});},
     ()=>{this.setState({savingScan:false});});
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
            Scan and Continue
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
              <Button transparent onPress={() => navigation.goBack()}>
                <Icon name="arrow-back" />
              </Button>
            </Left>
            <Body>
              <Title>SignUp</Title>
            </Body>
            <Right />
          </Header>
          <Content padder>
            <View padder>
              <Picker
                  style={{ color: commonColor.brandPrimary }}
                  itemStyle={styles.items}
                  selectedValue={this.state.location}
                  onValueChange={(loc) => this.updateLocation(loc)}>
                      {this.state.locations.map((l, i) => {return <Picker.Item value={l} label={l.name} key={i}  />; })}
              </Picker>
              <Picker
                  style={{ color: commonColor.brandPrimary }}
                  itemStyle={styles.items}
                  selectedValue={this.state.unit}
                  onValueChange={(unit) => this.updateUnit(unit)}>
                      {this.state.units.map((l, i) => {return <Picker.Item value={l} label={l.name} key={i}  />; })}
              </Picker>
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
                <Text style={{ color: commonColor.brandPrimary }} />
                )
              }
            </View>
            <Spinner visible={this.state.loadingUnits} textContent={"Loading Units..."} textStyle={{color: "#FFF"}} />
            <Spinner visible={this.state.loadingData} textContent={"Loading Locations..."} textStyle={{color: "#FFF"}} />
            <Spinner visible={this.state.loadingUsers} textContent={"Loading Users..."} textStyle={{color: "#FFF"}} />
            <Spinner visible={this.state.serverCalled} textContent={"Storing data..."} textStyle={{color: "#FFF"}} cancelable={true} />
          </Content>
        </Image>
      </Container>
    );
  }
}

export default connect()(SignUp);
