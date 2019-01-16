const React = require("react-native");
const { Dimensions, Platform } = React;

const deviceHeight = Dimensions.get("window").height;
const commonColor = require("../../theme/variables/commonColor");

export default {
  containerImage: {
    flex: 1,
    width: null,
    height: null
  },
  logoShadowImage: {
    flex: 1,
    marginTop: deviceHeight < 600 ? -40 : -10,
    width: null,
    height: null,
    backgroundColor: "transparent"
  },
  bgView: {
    flex: 1,
    marginTop: deviceHeight / 2 - 15,
    backgroundColor: commonColor.brandPrimary,
    paddingTop: 20,
    paddingLeft: 20,
    paddingRight: 20,
    paddingBottom: Platform.OS === "ios" ? 50 : 50
  },
  inputGrp: {
    flexDirection: "row",
    marginBottom: 10
  },
  input: {
    paddingLeft: 10,
    color: commonColor.textColor
  },
  formErrorIcon: {
    color: commonColor.textColor,
    marginTop: 5,
    right: 10
  },
  formErrorText1: {
    fontSize: Platform.OS === "android" ? 12 : 15,
    color: commonColor.brandDanger,
    textAlign: "right",
    top: -10
  },
  formErrorText2: {
    fontSize: Platform.OS === "android" ? 12 : 15,
    color: "transparent",
    textAlign: "right",
    top: -10
  },
  container: {
      flex: 1,
      justifyContent: "center",
      alignItems: "center",
      marginTop: 70
   },
   activityIndicator: {
    position: "absolute",
    left: 0,
    right: 0,
    top: 0,
    bottom: 0,
    alignItems: "center"
   }
};
