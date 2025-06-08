import './App.css';
import {React, useState} from "react";
import {BrowserRouter, Route, Routes, Navigate} from "react-router-dom";
import NavigationBar from "./components/NavigationBar";
import Home from "./components/Home";
import Login from "./components/Login";
import Utils from "./utils/Utils";
import SideBar from "./components/SideBar";
import CountryListComponent from "./components/CountryListComponent";
import CountryComponent from "./components/CountryComponent";
import MyAccountComponent from "./components/MyAccountComponent";
import UserListComponent from "./components/UserListComponent";
import UserComponent from "./components/UserComponent";
import PaintingComponent from "./components/PaintingComponent";
import PaintingListComponent from "./components/PaintingListComponent";
import MuseumComponent from "./components/MuseumComponent";
import MuseumListComponent from "./components/MuseumListComponent";
import ArtistComponent from "./components/ArtistComponent";
import ArtistListComponent from "./components/ArtistListComponent";
import {connect} from "react-redux";


const ProtectedRoute = ({children}) => {
    let user = Utils.getUser();
    console.log("ProtectedRoute user:", user);
    return user ? children : <Navigate to={'/login'} />
};

function mapStateToProps(state) {
    const { msg } = state.alert;
    return { error_message: msg };
}

function mapStateToPropsForRoute(state) {
    return {
        user: state.authentication.user
    };
}


const ConnectedProtectedRoute = connect(mapStateToPropsForRoute)(ProtectedRoute);

const App = props => {

    const [exp,setExpanded] = useState(true);
    return (
        <div className="App">
            <BrowserRouter>
                <NavigationBar toggleSideBar={() =>
                    setExpanded(!exp)}/>
                <div className="wrapper">
                    <SideBar expanded={exp} />
                    <div className="container-fluid">
                        { props.error_message &&  <div className="alert alert-danger m-1">{props.error_message}</div>}
                        <Routes>
                            <Route path="/" element={<Navigate to="/home" />} />
                            <Route path="login" element={<Login />}/>
                            <Route path="home" element={<ConnectedProtectedRoute><Home/></ConnectedProtectedRoute>}/>
                            <Route path="countries" element={<ConnectedProtectedRoute><CountryListComponent/></ConnectedProtectedRoute>}/>
                            <Route path="countries/:id" element={<ConnectedProtectedRoute><CountryComponent/></ConnectedProtectedRoute>}/>
                            <Route path="artists" element={<ConnectedProtectedRoute><ArtistListComponent/></ConnectedProtectedRoute>}/>
                            <Route path="artists/:id" element={<ConnectedProtectedRoute><ArtistComponent/></ConnectedProtectedRoute>}/>
                            <Route path="museums" element={<ConnectedProtectedRoute><MuseumListComponent/></ConnectedProtectedRoute>}/>
                            <Route path="museums/:id" element={<ConnectedProtectedRoute><MuseumComponent/></ConnectedProtectedRoute>}/>
                            <Route path="paintings" element={<ConnectedProtectedRoute><PaintingListComponent/></ConnectedProtectedRoute>}/>
                            <Route path="paintings/:id" element={<ConnectedProtectedRoute><PaintingComponent/></ConnectedProtectedRoute>}/>
                            <Route path="users" element={<ConnectedProtectedRoute><UserListComponent/></ConnectedProtectedRoute>}/>
                            <Route path="users/:id" element={<ConnectedProtectedRoute><UserComponent/></ConnectedProtectedRoute>}/>
                            <Route path="myaccount" element={<ConnectedProtectedRoute><MyAccountComponent/></ConnectedProtectedRoute>}/>
                        </Routes>
                    </div>
                </div>
            </BrowserRouter>
        </div>
    );
}


export default connect(mapStateToProps)(App);
