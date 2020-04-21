import React from 'react';
import ReactDOM from 'react-dom';
import Login from "./Login";

if(document.getElementById('app')){
    ReactDOM.render(<Login />, document.getElementById('app'));
}