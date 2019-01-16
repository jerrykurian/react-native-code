import React from "react";
import { StackNavigator, DrawerNavigator } from "react-navigation";
import { Root } from "native-base";

import Login from "./screens/Login";
import SignUp from "./screens/SignUp";
import Icons from "./screens/Icons/";
import Scan from "./screens/Scan/";
import SideBar from "./screens/Sidebar";
import FooterTabNavigation from "./components/Footer/tabNavigation";

const Drawer = DrawerNavigator(
  {
    FooterTabNavigation: { screen: FooterTabNavigation },
    Register: { screen: SignUp },
    Scan: { screen: Scan }
  },
  {
    initialRouteName: "FooterTabNavigation",
    contentOptions: {
      activeTintColor: "#e91e63"
    },
    contentComponent: props => <SideBar {...props} />
  }
);

const App = StackNavigator(
  {
    Login: { screen: Login },
    SignUp: { screen: SignUp },
    Drawer: { screen: Drawer }
  },
  {
    index: 0,
    initialRouteName: "Login",
    headerMode: "none"
  }
);

export default () =>
  <Root>
    <App />
  </Root>;
