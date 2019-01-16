import React, { Component } from "react";
import { Image, View } from "react-native";
import { connect } from "react-redux";
import {
  Container,
  Header,
  Title,
  Content,
  Button,
  Right,
  Left,
  Body,
  Icon
} from "native-base";
import styles from "./styles";

const glow2 = require("../../../assets/glow2.png");

class Icons extends Component {
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
              <Title>Icons</Title>
            </Body>
            <Right />
          </Header>

          <Content padder>
            <View style={styles.iconContainer}>
              <Icon active name="logo-apple" style={styles.icon} />
              <Icon name="pizza" style={styles.icon} />
              <Icon name="person" style={styles.icon} />
              <Icon active name="beer" style={styles.icon} />
              <Icon name="bicycle" style={styles.icon} />
              <Icon name="navigate" style={styles.icon} />
              <Icon active name="cloud-circle" style={styles.icon} />
              <Icon name="pie" style={styles.icon} />
              <Icon name="bookmarks" style={styles.icon} />
              <Icon active name="pulse" style={styles.icon} />
              <Icon name="camera" style={styles.icon} />
              <Icon active name="mic-off" style={styles.icon} />
              <Icon active name="film" style={styles.icon} />
              <Icon active name="flame" style={styles.icon} />
              <Icon name="paper" style={styles.icon} />
              <Icon active name="paper-plane" style={styles.icon} />
              <Icon name="speedometer" style={styles.icon} />
              <Icon name="cart" style={styles.icon} />
              <Icon name="flask" style={styles.icon} />
              <Icon active name="cloudy-night" style={styles.icon} />
              <Icon active name="partly-sunny" style={styles.icon} />
              <Icon name="paw" style={styles.icon} />
              <Icon active name="rose" style={styles.icon} />
              <Icon name="pint" style={styles.icon} />
              <Icon active name="shuffle" style={styles.icon} />
              <Icon active name="game-controller-a" style={styles.icon} />
              <Icon name="glasses" style={styles.icon} />
              <Icon active name="microphone" style={styles.icon} />
              <Icon active name="keypad" style={styles.icon} />
              <Icon name="color-filter" style={styles.icon} />
              <Icon name="eye" style={styles.icon} />
              <Icon active name="mic-off" style={styles.icon} />
              <Icon name="alarm" style={styles.icon} />
              <Icon active name="medkit" style={styles.icon} />
              <Icon name="ionic" style={styles.icon} />
              <Icon active name="star-half" style={styles.icon} />
              <Icon active name="refresh" style={styles.icon} />
              <Icon active name="train" style={styles.icon} />
              <Icon active name="musical-notes" style={styles.icon} />
              <Icon active name="wine" style={styles.icon} />
              <Icon active name="nutrition" style={styles.icon} />
              <Icon name="thunderstorm" style={styles.icon} />
              <Icon name="grid" style={styles.icon} />
              <Icon active name="settings" style={styles.icon} />
              <Icon active name="chatbubbles" style={styles.icon} />
              <Icon active name="chatboxes" style={styles.icon} />
              <Icon name="cog" style={styles.icon} />
              <Icon name="baseball" style={styles.icon} />
              <Icon name="bulb" style={styles.icon} />
              <Icon active name="calculator" style={styles.icon} />
              <Icon active name="rainy" style={styles.icon} />
              <Icon name="videocam" style={styles.icon} />
              <Icon name="play" style={styles.icon} />
              <Icon name="disc" style={styles.icon} />
              <Icon active name="body" style={styles.icon} />
              <Icon active name="lock" style={styles.icon} />
              <Icon name="skip-backward" style={styles.icon} />
              <Icon active name="key" style={styles.icon} />
              <Icon name="flag" style={styles.icon} />
              <Icon active name="skip-forward" style={styles.icon} />
              <Icon name="barcode" style={styles.icon} />
              <Icon active name="logo-tux" style={styles.icon} />
              <Icon name="copy" style={styles.icon} />
              <Icon active name="stopwatch" style={styles.icon} />
              <Icon name="medical" style={styles.icon} />
              <Icon active name="archive" style={styles.icon} />
              <Icon active name="bookmark" style={styles.icon} />
              <Icon active name="clipboard" style={styles.icon} />
              <Icon active name="happy" style={styles.icon} />
              <Icon active name="share" style={styles.icon} />
              <Icon active name="bluetooth" style={styles.icon} />
              <Icon active name="search" style={styles.icon} />
              <Icon active name="wifi" style={styles.icon} />
              <Icon name="hand" style={styles.icon} />
              <Icon active name="trash" style={styles.icon} />
              <Icon active name="images" style={styles.icon} />
              <Icon active name="attach" style={styles.icon} />
              <Icon active name="logo-facebook" style={styles.icon} />
              <Icon active name="logo-googleplus" style={styles.icon} />
              <Icon active name="logo-twitter" style={styles.icon} />
              <Icon active name="logo-github" style={styles.icon} />
              <Icon active name="logo-whatsapp" style={styles.icon} />
              <Icon active name="logo-wordpress" style={styles.icon} />
              <Icon active name="logo-youtube" style={styles.icon} />
            </View>
          </Content>
        </Image>
      </Container>
    );
  }
}

export default connect()(Icons);
