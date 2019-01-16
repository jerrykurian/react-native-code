import React from "react";
import { Icon, FooterTab, Button, Footer as FTab } from "native-base";
import { TabNavigator } from "react-navigation";
import Home from "../../screens/Home/";
import styles from "./styles";

const FooterTabNavigation = TabNavigator(
  {
    Home: {
      screen: ({ screenProps, navigation }) => <Home navigation={navigation} />
    }
  },
  {
    tabBarPosition: "bottom",
    lazy: true,
    tabBarComponent: props => {
      return (
        <FTab>
          <FooterTab style={styles.footer}>
            <Button onPress={() => props.navigation.navigate("Home")}>
              <Icon name="add-circle" style={{ color: "#fff" }} />
            </Button>
          </FooterTab>
        </FTab>
      );
    }
  }
);

export default FooterTabNavigation;
