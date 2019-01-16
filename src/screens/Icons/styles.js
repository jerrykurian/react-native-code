const React = require("react-native");
const { Dimensions } = React;
const deviceWidth = Dimensions.get("window").width;

export default {
  containerImage: {
    flex: 1,
    width: null,
    height: null
  },
  iconContainer: {
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-around",
    paddingLeft: 15
  },
  icon: {
    width: deviceWidth / 8,
    height: deviceWidth / 8,
    justifyContent: "center"
  }
};
