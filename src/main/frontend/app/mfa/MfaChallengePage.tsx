import React, {useEffect, useState} from "react";
import ErrorBanner from "../component/ErrorBanner";
import {getMfaMethods, MfaAccountEnrolledMethod, sendMfaCode} from "./MfaRepository";
import EmailIcon from "@mui/icons-material/Email";
import {
    Box,
    Chip,
    Divider,
    Grid,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableRow,
    ThemeProvider,
    Typography
} from "@mui/material";
import theme from "../component/styles";
import Modal from "../component/Modal";
import Template from "../component/Template";
import {Person, VpnKey} from "@mui/icons-material";
import FormInputTextField from "../component/FormInputTextField";
import Separator from "../component/Separator";
import FormButton from "../component/FormButton";

interface MfaChallengePageProps {
    rawI18nMessages: string
    rawErrors: string
    hasServerSideErrors: boolean
    csrfName: string
    csrfToken: string
}

const MfaChallengePage: React.FC<MfaChallengePageProps> = ({
                                                               rawI18nMessages,
                                                               rawErrors,
                                                               hasServerSideErrors,
                                                               csrfName,
                                                               csrfToken
                                                           }) => {
    const errorMessage = JSON.parse(rawErrors)["feedback"];
    const i18nMessages = JSON.parse(rawI18nMessages)

    const errorsBanner = <ErrorBanner errorMessage={errorMessage}/>
    const [openChooseMFAModal, setOpenChooseMFAModal] = React.useState(false)
    const [mfaAccountEnrolledMethod, setMfaAccountEnrolledMethod] = useState<MfaAccountEnrolledMethod[]>()
    const handleCloseChooseMFAModal = () => {
        setOpenChooseMFAModal(false);
    };
    const handleOpenChooseMFAModal = () => {
        setOpenChooseMFAModal(true);
    }

    useEffect(() => {
        getMfaMethods()
            .then(result => setMfaAccountEnrolledMethod(result))
    }, [])

    const mfaIcon = (mfaMethod: MfaAccountEnrolledMethod) => {
        let icon
        if ("EMAIL_MFA_METHOD" === mfaMethod.mfaMethod) {
            icon = <><EmailIcon/> EMail: {mfaMethod.mfaChannel}</>
        }
        return icon
    }
    return (
        <ThemeProvider theme={theme}>

            <Modal maxWidth="sm"
                   open={openChooseMFAModal}
                   onExecute={handleCloseChooseMFAModal}
                   onExecuteButtonLabel={i18nMessages["changeMfaMethodModalExecuteButtonText"]}
                   onClose={handleCloseChooseMFAModal}
                   onCloseButtonLabel={i18nMessages["changeMfaMethodModalCloseButtonText"]}
                   headerLabel={i18nMessages["changeMfaMethodModalHeaderText"]}
                   title={i18nMessages["changeMfaMethodModalTitleText"]}>
                <TableContainer component={Paper}>
                    <Table sx={{
                        width: '100%',
                        overflowX: 'auto'
                    }} aria-label="simple table">
                        <TableBody>
                            {mfaAccountEnrolledMethod?.map(method =>
                                <TableRow style={{
                                    // width: '100%',
                                    overflowX: 'auto'
                                }}>
                                    <TableCell component="th" scope="row">
                                        {mfaIcon(method)}
                                    </TableCell>
                                    <TableCell align="right"> <Chip label="Chip Filled"/></TableCell>
                                    <TableCell align="right"> <Chip label="Chip Filled"/></TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Modal>

            <Template maxWidth="sm">
                <Typography variant="h3" component="h3">
                    <VpnKey fontSize="large"/> {i18nMessages["pageTitleText"]}
                </Typography>

                <Grid style={{marginTop: '10px'}}>
                    <Divider/>
                </Grid>
                {hasServerSideErrors ? errorsBanner : ""}

                {<form action="mfa-challenge" method="post">
                    <Box>
                        <input name="mfa-method" type="hidden" value="EMAIL_MFA_METHOD"/>
                        <input name={csrfName} type="hidden" value={csrfToken}/>

                        <FormInputTextField id="mfa-code"
                                            label={i18nMessages["mfaPlaceholderText"]}
                                            type="text"
                                            suffix={<Person fontSize="large"/>}/>

                        <Separator/>

                        <FormButton type="submit" label={i18nMessages["submitButtonText"]}
                                    buttonColor={"success"}/>

                        <Separator/>

                        <Grid container sm={12}>
                            <Grid item sm={4}>
                                <FormButton type="button" label={i18nMessages["sendAgainButtonText"]}
                                            onClickHandler={() => sendMfaCode()}/>
                            </Grid>
                            <Grid item sm={4}> </Grid>
                            <Grid item sm={4}>
                                <FormButton type="button" label={i18nMessages["changeMfaMethodButtonText"]}
                                            direction={"rtl"}
                                            onClickHandler={() => handleOpenChooseMFAModal()}/>
                            </Grid>
                        </Grid>
                    </Box>
                </form>}
            </Template>
        </ThemeProvider>

    )
}

export default MfaChallengePage