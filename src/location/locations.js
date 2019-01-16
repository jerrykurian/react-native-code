export default {

  loadLocations(callback, errorckb, title = "Please Select"){
    var fetchServicesUri = "http://tfpapi.learnym.com/api/locations";
    fetch(fetchServicesUri)
      .then((response) => response.json())
      .then((responseJson) => {
          responseJson.unshift({name:title, id:-1});
          callback(responseJson);
      })
      .catch((error) => {
          errorckb();
      }).done();
    }
};

