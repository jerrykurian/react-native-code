import React, { Component } from "react";
import { Image, AsyncStorage } from "react-native";
import { connect } from "react-redux";
import {
  Container,
  Header,
  Title,
  Content,
  Button,
  Icon,
  ListItem,
  Text,
  Left,
  Right,
  Body,
  List,
  Picker,
  View
} from "native-base";
import Spinner from "react-native-loading-spinner-overlay";
import styles from "./styles";
import { itemsFetchData } from "../../actions";
import data from "./data.json";
import FPScanBridge from "../../bridge/fpScanNativeModule";
const commonColor = require("../../theme/variables/commonColor");

const glow2 = require("../../../assets/glow2.png");

class Home extends Component {
  constructor(props) {
    super(props);
  }
  componentWillMount(){
    this.props.fetchData(data);
  }
  // The main render method
  render() {
    if (this.props.isLoading) {
      return <Spinner />;
    } else {
      const navigation = this.props.navigation;
      return (
        <Container>
         <Content>
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
                <Title>Home</Title>
              </Body>
              <Right />
            </Header>
            <Content padder>
              <List
                dataArray={this.props.items}
                renderRow={(
                    data // eslint-disable-line
                  ) =>
                  <ListItem icon style={styles.listitem} button onPress={() =>
                    {this.props.navigation.navigate(data.link);}}>
                    <Left>
                      <Icon active name={data.icon} style={{ width: 30 }} />
                    </Left>
                    <Body>
                      <Text>
                        {data.listData}
                      </Text>
                    </Body>
                  </ListItem>
                }
              />
            </Content>
          </Image>
          </Content>
        </Container>
      );
    }
  }
}

function bindAction(dispatch) {
  return {
    fetchData: url => dispatch(itemsFetchData(url))
  };
}
const mapStateToProps = state => ({
  items: state.homeReducer.items,
  hasErrored: state.homeReducer.hasErrored,
  isLoading: state.homeReducer.isLoading
});
export default connect(mapStateToProps, bindAction)(Home);
