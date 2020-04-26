import React from 'react';
import ReactDOM from 'react-dom';
import {Paper, withStyles} from "@material-ui/core";
import Container from "@material-ui/core/Container";
import Typography from "@material-ui/core/Typography";
import {GroupAdd} from "@material-ui/icons";
import Divider from "@material-ui/core/Divider";


const styles = theme => ({
    margin: {
        margin: theme.spacing.unit * 2,
    },
    padding: {
        padding: theme.spacing.unit
    }
});


const AccountPage = withStyles(styles)((props) => {
    const {classes} = props;
    return (
        <Container maxWidth={"md"}>
            <Paper className={classes.padding} elevation={3}>
                <Typography variant="h3" component="h3">
                    <GroupAdd fontSize="large"/> Account Registration
                </Typography>

                <Divider/>

                <div className={classes.margin}>
                    <Typography variant="h3" component="h3">
                        Thank you to have been registered soon you will receive an email
                    </Typography>
                </div>
            </Paper>
        </Container>
    );
})

if (document.getElementById('app')) {
    ReactDOM.render(<AccountPage/>, document.getElementById('app'));
}