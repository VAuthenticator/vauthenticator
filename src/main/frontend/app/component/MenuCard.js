import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardContent from '@material-ui/core/CardContent';
import {CardHeader} from "@material-ui/core";
import {Link} from "react-router-dom";
import Grid from "@material-ui/core/Grid";

const useStyles = makeStyles({
    root: {
        maxWidth: 345
    },
    media: {
        height: 140,
    },
});

export default function MenuCard({title, content, linkTo}) {
    const classes = useStyles();
    return (
        <Grid item xs={4}>
            <Link to={linkTo} style={{textDecoration: 'none'}}>
                <Card className={classes.root}>
                    <CardActionArea>
                        <CardHeader
                            className={classes.media}

                            title={title}
                        />
                        <CardContent>
                            {content}
                        </CardContent>
                    </CardActionArea>
                </Card>
            </Link>
        </Grid>
    );
}