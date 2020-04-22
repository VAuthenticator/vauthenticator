import React from 'react';
import ReactDOM from 'react-dom';
import AccountPage from "./AccountPage";

if(document.getElementById('app')){
    ReactDOM.render(<AccountPage />, document.getElementById('app'));
}