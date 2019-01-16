import React, { Component } from "react";
import { Image, Platform, StatusBar } from "react-native";
import {
  Container,
  Content,
  Text,
  Item,
  Input,
  Button,
  Icon,
  View,
  Toast,
  Picker
} from "native-base";
import { Field, reduxForm } from "redux-form";
import styles from "./styles";
import FPScanBridge from "../../bridge/fpScanNativeModule";
import Spinner from "react-native-loading-spinner-overlay";
const commonColor = require("../../theme/variables/commonColor");
const backgroundImage = require("../../../assets/glow2.png");
const logo = require("../../../assets/logo.png");

const required = value => (value ? undefined : "Required");
const maxLength = max => value =>
  value && value.length > max ? `Must be ${max} characters or less` : undefined;
const maxLength15 = maxLength(15);
const minLength = min => value =>
  value && value.length < min ? `Must be ${min} characters or more` : undefined;
const minLength8 = minLength(8);
const email = value =>
  value && !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(value)
    ? "Invalid email address"
    : undefined;
const alphaNumeric = value =>
  value && /[^a-zA-Z0-9 ]/i.test(value)
    ? "Only alphanumeric characters"
    : undefined;

declare type Any = any;
class LoginForm extends Component {
  textInput: Any;
  constructor(props) {
    super(props);
    this.state = {
      loadingData: false,
      email:"",
      password:""
    };
    this.renderInput = this.renderInput.bind(this);
  }


  renderInput({ input, label, type, meta: { touched, error, warning } }) {
    return (
      <View>
        <Item error={error && touched} style={styles.inputGrp}>
          <Icon
            active
            name={input.name === "email" ? "mail" : "unlock"}
            style={{ color: "#fff" }}
          />
          <Input
            ref={c => (this.textInput = c)}
            placeholderTextColor="rgba(230,230,230,0.8)"
            style={styles.input}
            placeholder={input.name === "email" ? "Email" : "Password"}
            secureTextEntry={input.name === "password" ? true : false}
            {...input}
            onChangeText={(text) => (input.name === "email") ? this.setState({ email: text }) : this.setState({ password: text })}
          />
          {touched && error
            ? <Icon
                active
                style={styles.formErrorIcon}
                onPress={() => this.textInput._root.clear()}
                name="close"
              />
            : <Text />}
        </Item>
        {touched && error
          ? <Text style={styles.formErrorText1}>
              {error}
            </Text>
          : <Text style={styles.formErrorText2}>error here</Text>}
      </View>
    );
  }

  login() {
    this.setState({"loadingData": true});
    var data = {
     username: this.state.email,
     password: this.state.password
    };

    var fetchServicesUri = "http://tfpapi.learnym.com/api/authenticate";
    fetch(fetchServicesUri,
          {
            method: "POST",
            headers: {
             "Accept": "application/json",
             "Content-Type": "application/json"
            },
           body: JSON.stringify(data)
         })
        .then((response) => response.json())
        .then((responseJson) => {
          this.setState({"loadingData": false});
          this.props.navigation.navigate("Drawer");
        })
        .catch((error) => {
          this.setState({loadingUsers: false});
          this.setState({"loadingData": false});
          Toast.show({
            text: "Enter Valid Username & Password!",
            duration: 2500,
            position: "top",
            textStyle: { textAlign: "center" }
          });
        }).done();
  }

  render() {
    const { loadingData } = this.state;

    return (
      <Container>
        <StatusBar
          backgroundColor={
            Platform.OS === "android"
              ? commonColor.statusBarColor
              : "transparent"
          }
          barStyle="light-content"
        />
        <Content style={{ backgroundColor: commonColor.containerBackground }}>
          <Text style={{ color: commonColor.brandPrimary }}>Version 1.39</Text>
          <Image source={backgroundImage} style={styles.containerImage}>
            <Image source={logo} style={styles.logoShadowImage}>
              <View style={styles.bgView}>
                <Field
                  name="email"
                  component={this.renderInput}
                  type="email"
                  validate={[email, required]}
                />
                <Field
                  name="password"
                  component={this.renderInput}
                  type="password"
                  validate={[alphaNumeric, minLength8, maxLength15, required]}
                />
                <Button
                  light
                  rounded
                  block
                  style={{ marginBottom: 10 }}
                  onPress={() => this.login()}
                >
                  <Text style={{ color: commonColor.brandPrimary }}>Login</Text>
                </Button>
              </View>
            </Image>
            <Spinner visible={this.state.loadingData} textContent={"Loading..."} textStyle={{color: "#FFF"}} />
          </Image>
        </Content>
      </Container>
    );
  }
}
const Login = reduxForm({
  form: "login"
})(LoginForm);
export default Login;
