import React from 'react';
import ReactDOM from 'react-dom';
import {Button, Grid, Paper, TextField, withStyles} from "@material-ui/core";
import Container from "@material-ui/core/Container";
import Typography from "@material-ui/core/Typography";
import {ContactMail, Face, GroupAdd, Lock} from "@material-ui/icons";
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

                    <form action="signup" method="post">
                        <Grid container spacing={8} alignItems="flex-end">
                            <Grid item>
                                <ContactMail fontSize="large"/>
                            </Grid>
                            <Grid item md={true} sm={true} xs={true} lg={true}>
                                <TextField id="email" name="email" type="email" label="E-Mail" fullWidth={true}
                                           variant="outlined"/>
                            </Grid>
                        </Grid>

                        <Grid container spacing={8} alignItems="flex-end">
                            <Grid item>
                                <Lock fontSize="large"/>
                            </Grid>
                            <Grid item md={true} sm={true} xs={true}>
                                <TextField id="password" name="password" type="password" label="Password"
                                           fullWidth={true} variant="outlined"/>
                            </Grid>
                        </Grid>

                        <Grid container spacing={8} alignItems="flex-end">
                            <Grid item>
                                <Face fontSize="large"/>
                            </Grid>
                            <Grid item md={true} sm={true} xs={true}>
                                <TextField id="firstName" name="firstName" type="text" label="First Name"
                                           fullWidth={true} variant="outlined"/>
                            </Grid>
                        </Grid>

                        <Grid container spacing={8} alignItems="flex-end">
                            <Grid item>
                                <Face fontSize="large"/>
                            </Grid>
                            <Grid item md={true} sm={true} xs={true}>
                                <TextField id="lastName" name="lastName" type="text" label="Last Name" fullWidth={true}
                                           variant="outlined"/>
                            </Grid>
                        </Grid>

                        <Grid style={{marginTop: '10px'}}>
                            <Divider/>
                        </Grid>

                        <div dir="rtl">
                            <Grid container alignItems="flex-end" style={{marginTop: '10px'}}>
                                <Grid item md={true} sm={true} xs={true} justify="flex-end">
                                    <Button type={"submit"} variant="outlined" color="primary"
                                            style={{textTransform: "none"}}>Register to OnlyOne-Portal</Button>
                                </Grid>
                            </Grid>
                        </div>
                    </form>

                </div>

            </Paper>
        </Container>
    );
})

if (document.getElementById('app')) {
    ReactDOM.render(<AccountPage/>, document.getElementById('app'));
}