import ReactDOM from "react-dom";

export default (component: JSX.Element) => {
    let htmlElement = document.getElementById('app');
    if (htmlElement) {
        ReactDOM.render(component, htmlElement);
    }
}