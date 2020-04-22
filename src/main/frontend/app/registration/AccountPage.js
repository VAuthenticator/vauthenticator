import React from 'react';
import {Button, Grid, Paper, TextField, withStyles} from '@material-ui/core';
import {ContactMail, Face, GroupAdd, Lock} from '@material-ui/icons'
import Container from "@material-ui/core/Container";
import Divider from "@material-ui/core/Divider";
import Typography from "@material-ui/core/Typography";

const styles = theme => ({
    margin: {
        margin: theme.spacing.unit * 2,
    },
    padding: {
        padding: theme.spacing.unit
    }
});

class AccountPage extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const {classes} = this.props;
        return (
            <Container maxWidth={"md"}>
                <Paper className={classes.padding} elevation={3}>
                    <Typography variant="h3" component="h3">
                        <GroupAdd fontSize="large"/> Account Registration
                    </Typography>

                    <Divider/>

                    <div className={classes.margin}>

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
                                <TextField id="firstname" name="firstname" type="text" label="First Name"
                                           fullWidth={true} variant="outlined"/>
                            </Grid>
                        </Grid>

                        <Grid container spacing={8} alignItems="flex-end">
                            <Grid item>
                                <Face fontSize="large"/>
                            </Grid>
                            <Grid item md={true} sm={true} xs={true}>
                                <TextField id="lastname" name="lastname" type="text" label="Last Name" fullWidth={true}
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

                    </div>


                </Paper>
            </Container>
        );
    }
}

export default withStyles(styles)(AccountPage);