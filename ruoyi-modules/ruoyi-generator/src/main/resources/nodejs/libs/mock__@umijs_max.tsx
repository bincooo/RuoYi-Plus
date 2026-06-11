export function useIntl() {
    return {
        formatMessage(msg: { id: string; defaultMessage?: string }) {
            return msg.defaultMessage;
        },
    };
}
export function getLocale() {
    return "en-US";
}
export function useModel(str: string) {
    return {};
}
export function FormattedMessage(props: {
    key?: string;
    id: string;
    defaultMessage?: string;
}) {
    const intl = useIntl();
    return intl.formatMessage({
        id: props.id,
        defaultMessage: props.defaultMessage,
    });
}
