const spacing = (factor) => `${0.25 * factor}rem`

const vauthenticatorStyles = () => {
    return {
        margin: {
            margin: spacing(2)
        },

        padding: {
            padding: spacing(1)
        },

        root: {
            flexGrow: 1,
        },

        title: {
            flexGrow: 1,
        }
    }
}

export default vauthenticatorStyles