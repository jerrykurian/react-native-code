import React, { Component } from "react";
import { connect } from "react-redux";
import {
  Text,
  Icon,
  ListItem,
  Content,
  Left,
  Body,
  Right,
  Thumbnail,
  Badge
} from "native-base";
import { NavigationActions } from "react-navigation";
import styles from "./style";

const logo = require("../../../assets/icon2.png");
const resetAction = NavigationActions.reset({
  index: 0,
  actions: [NavigationActions.navigate({ routeName: "Login" })]
});

class SideBar extends Component {
  render() {
    const navigation = this.props.navigation;
    return (
      <Content style={{ backgroundColor: "#252A30" }}>
        <Thumbnail
          style={{
            alignSelf: "center",
            height: 200,
            width: 200,
            marginTop: 20,
            marginBottom: 15,
            resizeMode: "contain"
          }}
          square
          source={logo}
        />

        <ListItem
          button
          icon
          onPress={() => {
            navigation.navigate("FooterTabNavigation");
          }}
          style={styles.links}
        >
          <Left>
            <Icon name="home" />
          </Left>
          <Body>
            <Text>Home</Text>
          </Body>
          <Right />
        </ListItem>
        <ListItem
          button
          icon
          onPress={() => {
            navigation.navigate("SignUp");
          }}
          style={styles.links}
        >
          <Left>
            <Icon name="people" />
          </Left>
          <Body>
            <Text>Register</Text>
          </Body>
          <Right />
        </ListItem>
        <ListItem
          button
          icon
          onPress={() => {
            navigation.navigate("Scan");
          }}
          style={styles.links}
        >
          <Left>
            <Icon name="hand" />
          </Left>
          <Body>
            <Text>Attendance</Text>
          </Body>
          <Right />
        </ListItem>
        <ListItem
          button
          icon
          onPress={() => {
            navigation.dispatch(resetAction);
          }}
          style={styles.links}
        >
          <Left>
            <Icon name="ios-log-out" />
          </Left>
          <Body>
            <Text>LogOut</Text>
          </Body>
          <Right />
        </ListItem>
      </Content>
    );
  }
}

export default connect()(SideBar);
